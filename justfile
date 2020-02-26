set shell := ["bash", "-c"]

# tools bins
SEED      := 'seed-jvm'
BLOOP     := 'bloop'
COURSIER  := 'coursier'
SCALAFMT  := 'scalafmt-jvm'
SCALAFIX  := 'scalafix-jvm'


clean-compile: clean reload build

reload:
    {{SEED}} all

# supply a specific TARGET to not build everything
build TARGET='all':
    @if [ {{TARGET}} == all ]; then \
        echo 'Building all targets...'; \
        {{SEED}} build protocol server client; \
    else \
        echo 'Building only specified target...'; \
        {{SEED}} build {{TARGET}}; \
    fi

# supply MODE=all really clean the workspace as if fresh git clone
clean MODE='':
    @echo 'Removing build related artifacts...'
    @rm --recursive --force --verbose build .bloop .idea out

    @if [ {{MODE}} == all ]; then \
        echo 'Removing extra stuff from workspace...'; \
        rm --recursive --force --verbose .scalafix-rules/*; \
    fi

run APP: build
    {{BLOOP}} run {{APP}}

check-format:
    {{SCALAFMT}} --git true --list --test --stdout

check-lint:
    #!/usr/bin/env bash
    TOOL_CLASSPATH="$({{COURSIER}} fetch --cache .scalafix-rules com.github.vovapolu:scaluzzi_2.12:0.1.3 -p)"

    {{SCALAFIX}} --tool-classpath="$TOOL_CLASSPATH" --check


