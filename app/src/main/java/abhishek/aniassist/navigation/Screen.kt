package abhishek.aniassist.navigation

object Screen {
    const val SPLASH          = "splash"
    const val ONBOARDING      = "onboarding"
    const val LOGIN           = "login"
    const val SIGNUP          = "signup"
    const val FORGOT_PASSWORD = "forgot_password"
    const val GET_LOCATION    = "get_location"
    const val HOME            = "home"
    const val LOST            = "lost"
    const val FOUND           = "found"
    const val POST            = "post"
    const val REPORT_CHOOSER  = "report_chooser"
    const val SUCCESS         = "success/{type}"
    const val PARTICULAR_LOST   = "particular_lost"
    const val PARTICULAR_FOUND  = "particular_found"
    const val PARTICULAR_POST   = "particular_post"
    const val VERIFY          = "verify/{aniId}/{city}/{type}"
    const val PROFILE         = "profile"
    const val TRIVIA          = "trivia"
    const val NEARBY_CASES    = "nearby_cases"
    const val MAP_PICKER      = "map_picker"
    const val FULL_SCREEN     = "full_screen"
    const val CAMERA          = "camera/{front}"

    fun verifyRoute(aniId: String, city: String, type: String) =
        "verify/$aniId/$city/$type"

    fun successRoute(type: String) = "success/$type"
    fun cameraRoute(front: Boolean) = "camera/$front"
}
