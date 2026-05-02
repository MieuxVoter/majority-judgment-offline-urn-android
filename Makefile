# https://www.gnu.org/software/make/manual/make.html

.DEFAULT_GOAL := help
.PHONY: help

lint:  ## Lint and auto-correct using detekt.
	./gradlew :app:detekt --auto-correct --rerun

help:  ## Show this help.
	@echo "Welcome to your favorite Majority Judgment application for Android!\n"
	@echo "Usage:   make <goal>\n"
	@echo "Available goals:"
	@sed \
		-e '/^[a-zA-Z0-9._-]*:.*##/!d' \
		-e 's/:.*##\s*/:/' \
		-e 's/^\(.\+\):\(.*\)/$(shell tput setaf 6)\1$(shell tput sgr0):\2/' \
		$(MAKEFILE_LIST) | column -c2 -t -s :
	@echo "\nNote: We use gradlew.  For more available tasks, run   ./gradlew tasks"
