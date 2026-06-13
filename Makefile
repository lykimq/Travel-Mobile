.PHONY: help devices dev apk release install clean check-device

PACKAGE  := hydra.travel.sync.dev
ACTIVITY := $(PACKAGE)/com.travelsync.MainActivity
GRADLE   := ./gradlew

# Prefer physical phone over emulator; override with ANDROID_SERIAL=<id>
ADB_DEVICE := $(if $(ANDROID_SERIAL),$(ANDROID_SERIAL),$(shell adb devices 2>/dev/null | awk '/device$$/ && !/^emulator-/ {print $$1; exit}'))
ifeq ($(ADB_DEVICE),)
  ADB_DEVICE := $(shell adb devices 2>/dev/null | awk '/device$$/ {print $$1; exit}')
endif
ADB := adb -s $(ADB_DEVICE)

WATCH_PATHS := app/src/main/java app/src/main/res app/src/main/AndroidManifest.xml app/build.gradle.kts build.gradle.kts gradle/libs.versions.toml

help:
	@echo "================================================================="
	@echo "             TravelSync USB Deployment Toolchain                 "
	@echo "================================================================="
	@echo "Available commands:"
	@echo "  make devices   - List USB-connected Android devices (adb)"
	@echo "  make dev       - Install, launch, then watch and redeploy on changes"
	@echo "  make apk       - Build the debug APK"
	@echo "  make release   - Install debug APK on the connected device and launch"
	@echo "  make install   - Install debug APK only (no launch)"
	@echo "  make clean     - Clean gradle build caches and artifacts"
	@echo "================================================================="

check-device:
	@adb devices | grep -q 'device$$' || (echo "No Android device found."; exit 1)
	@test -n "$(ADB_DEVICE)" || (echo "Could not resolve a target device."; exit 1)

devices:
	@echo "Checking for connected USB devices..."
	adb devices

dev: check-device
	@command -v inotifywait >/dev/null || (echo "Install inotify-tools: sudo apt install inotify-tools"; exit 1)
	@echo "Device: $(ADB_DEVICE)"
	ANDROID_SERIAL=$(ADB_DEVICE) $(GRADLE) installDebug
	$(ADB) shell am start -n $(ACTIVITY)
	@echo "Watching for changes... (Ctrl+C to stop)"
	@while inotifywait -r -e close_write,create,delete,move $(WATCH_PATHS) 2>/dev/null; do \
		echo "--- Change detected, deploying ---"; \
		ANDROID_SERIAL=$(ADB_DEVICE) $(GRADLE) installDebug -q && \
		$(ADB) shell am force-stop $(PACKAGE) && \
		$(ADB) shell am start -n $(ACTIVITY) && \
		echo "--- Done ---"; \
	done

apk:
	$(GRADLE) assembleDebug
	@echo "APK: app/build/outputs/apk/debug/app-debug.apk"

release: check-device
	ANDROID_SERIAL=$(ADB_DEVICE) $(GRADLE) installDebug
	$(ADB) shell am start -n $(ACTIVITY)
	@echo "TravelSync installed and launched on $(ADB_DEVICE)"

install: check-device
	ANDROID_SERIAL=$(ADB_DEVICE) $(GRADLE) installDebug

clean:
	@echo "Cleaning gradle caches..."
	$(GRADLE) clean
