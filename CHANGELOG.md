# Version 1.1

- The architecture of the app is migrated from MVVM to MVI.
- The "delete library" dialog is replaced with a more UX-friendly "Swipe to delete" gesture.
- Text changes now happen with a nice fade through animation
- Retrofit is replaced with Ktor
- The dependency management mechanism is migrated from "buildSrc" to Gradle's "version catalogs"

# Version 1.0

- Added tests for LibrariesViewModel
- Added tests for AddLibraryScreen, LibrariesListScreen, Toolbar, SearchBar, SortOrderDialog,
  DeleteLibraryDialog, and NightModeDialog composables
- Replaced SharedPreferences with DataStore

# Version 0.3

- The UI is implemented from scratch using Compose!
