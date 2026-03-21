# For producing the release announcements from the templates, you will need
# jinja2 and jinja2-cli.

SHELL:=/bin/bash

# Prefer an installed JDK 25 from SDKMAN or ~/.jdk when the current Java is too old; the build targets release 25.
SDKMAN_JAVA_25 := $(lastword $(sort $(wildcard $(HOME)/.sdkman/candidates/java/25*)))
JDK_JAVA_25 := $(lastword $(sort $(wildcard $(HOME)/.jdk/jdk-25*)))
JAVA_MAJOR := $(shell if [ -n "$$JAVA_HOME" ] && [ -x "$$JAVA_HOME/bin/java" ]; then "$$JAVA_HOME/bin/java" -version 2>&1; else java -version 2>&1; fi | sed -n 's/.*version "\\([0-9][0-9]*\\).*/\\1/p' | head -n1)

ifneq ($(origin JAVA_HOME), command line)
ifneq ($(SDKMAN_JAVA_25),)
ifeq ($(shell [ -n "$(JAVA_MAJOR)" ] && [ "$(JAVA_MAJOR)" -ge 25 ] && echo yes),)
export JAVA_HOME := $(SDKMAN_JAVA_25)
export PATH := $(JAVA_HOME)/bin:$(PATH)
endif
else ifneq ($(JDK_JAVA_25),)
ifeq ($(shell [ -n "$(JAVA_MAJOR)" ] && [ "$(JAVA_MAJOR)" -ge 25 ] && echo yes),)
export JAVA_HOME := $(JDK_JAVA_25)
export PATH := $(JAVA_HOME)/bin:$(PATH)
endif
endif
endif

LIBDIR:=target/lib
TMPDIR:=tmp
DISTDIR:=dist
JDOCDIR:=jdoc

# numeric part of the version only
VNUM:=3.8.0
# major-minor part of the version
V_MAJ_MIN:=$(shell echo "$(VNUM)" | cut -f1,2 -d'.')

MAVEN_VERSION:=$(VNUM)-SNAPSHOT
#MAVEN_VERSION:=$(VNUM)-beta5
#MAVEN_VERSION:=$(VNUM)

JARNAME:=vassal-app-$(MAVEN_VERSION)

#Slashes are substituted so as not to create subdirectories
RAW_GITBRANCH := $(shell git branch --show-current 2>/dev/null)
GITBRANCH := $(if $(RAW_GITBRANCH),$(subst /,_,$(RAW_GITBRANCH)),worktree)
HAS_GIT_HEAD := $(shell git rev-parse --verify HEAD >/dev/null 2>&1 && echo yes)
GITCOMMIT := $(if $(HAS_GIT_HEAD),$(shell git rev-parse --short HEAD 2>/dev/null),nogit)
GITTAG := $(if $(HAS_GIT_HEAD),$(shell git describe --tags 2>/dev/null),)

ifeq ($(GITTAG), $(MAVEN_VERSION))
  # we are at a release tag
  VERSION:=$(MAVEN_VERSION)
else ifeq ($(patsubst release-%,release,$(GITBRANCH)), release)
  # we are on a release branch
  VERSION:=$(MAVEN_VERSION)-$(GITCOMMIT)
else
  # we are on a branch
  VERSION:=$(MAVEN_VERSION)-$(GITCOMMIT)-$(GITBRANCH)
endif

ifeq ($(wildcard .mvn/wrapper/maven-wrapper.properties),)
MVN:=mvn
else
MVN:=./mvnw
endif

SKIPS:=

# -Dspotbugs.skip=true
# -Dlicense.skipDownloadLicenses
# -Dclirr.skip=true
# -Dmaven.javadoc.skip=true
# -Dpmd.skip=true
# -Dmaven.test.skip=true

jar: SKIPS:=-Dspotbugs.skip=true -Dlicense.skipDownloadLicenses
jar: $(LIBDIR)/Vengine.jar

compile:
	$(MVN) compile

version-set:
	$(MVN) versions:set -DnewVersion=$(MAVEN_VERSION) -DgenerateBackupPoms=false

test:
	$(MVN) test

lint:
	$(MVN) spotless:check checkstyle:check

format:
	$(MVN) spotless:apply

precommit:
	$(MAKE) lint
	$(MAKE) test

$(TMPDIR) $(JDOCDIR):
	mkdir -p $@

$(LIBDIR)/Vengine.jar: version-set
	mkdir -p $(LIBDIR)
	$(MVN) deploy -DgitVersion=$(VERSION) $(SKIPS)
	cp target/$(JARNAME).jar $@

$(TMPDIR)/notes.json: $(DISTDIR)/notes/data.json | $(TMPDIR)
	jinja2 --strict -Dversion=$(VERSION) -Dversion_feature=$(V_MAJ_MIN) -o $@ $^

$(TMPDIR)/NOTES-%: $(DISTDIR)/notes/NOTES-%.jinja $(TMPDIR)/notes.json | $(TMPDIR)
	jinja2 --strict -Dversion=$(VERSION) -Dversion_feature=$(V_MAJ_MIN) -o $@ $^

release-announcements: $(TMPDIR)/NOTES-bgg $(TMPDIR)/NOTES-csw $(TMPDIR)/NOTES-news $(TMPDIR)/NOTES-fb $(TMPDIR)/NOTES-gh

clean-release:
	$(RM) -r $(TMPDIR)/* $(LIBDIR)/Vengine.jar

post-release: version-set

target/$(JARNAME)-javadoc.jar: $(LIBDIR)/Vengine.jar

javadoc: target/$(JARNAME)-javadoc.jar | $(JDOCDIR)
	pushd $(JDOCDIR) ; unzip ../target/$(JARNAME)-javadoc.jar ; popd

version-print:
	@echo $(VERSION)

clean-javadoc:
	$(RM) -r $(JDOCDIR)

clean: clean-release
	$(MVN) clean

# prevents make from trying to delete intermediate files
.SECONDARY:

.PHONY: compile test lint format precommit clean release-announcements clean-release post-release javadoc jar clean-javadoc version-set version-print
