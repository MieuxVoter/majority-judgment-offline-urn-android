## How to add a new screen

1. Create a `@Composable` function in `app/src/main/java/com/illiouchine/jm/ui/screen/`.
2. Create a `ViewModel` in `app/src/main/java/com/illiouchine/jm/logic/`.
3. Add the view model to `koin` in `app/src/main/java/com/illiouchine/jm/MajorityUrnApplication.kt`.
4. Add a data class/object in `app/src/main/java/com/illiouchine/jm/ui/navigator/Screens.kt`.
5. Add the entry in `app/src/main/java/com/illiouchine/jm/MainActivity.kt`.
