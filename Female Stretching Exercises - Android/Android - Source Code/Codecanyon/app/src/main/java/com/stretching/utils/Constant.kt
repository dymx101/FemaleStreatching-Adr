package com.stretching.utils

import android.os.Environment
import java.io.File


internal object Constant {


    val CHECK_LB_KG = "check_lb_kg"
    val FINISH_ACTIVITY = "finish_activity"
    val BANNER_TYPE = "BANNER_TYPE"
    val IS_PURCHASE = "is_purchase"
    val REC_BANNER_TYPE = "REC_BANNER_TYPE"
    val FOLDER_NAME = "Stretching Exercises"
    val CACHE_DIR = ".StretchingExercises/Cache"
    val TMP_DIR = (Environment
        .getExternalStorageDirectory().absolutePath
            + File.separator
            + FOLDER_NAME + "/tmp")
    val PATH = Environment.getExternalStorageDirectory()
        .absolutePath + File.separator + "" + FOLDER_NAME
    val FOLDER_RIDEINN_PATH = (Environment
        .getExternalStorageDirectory().absolutePath
            + File.separator
            + ".StretchingExercises")
    val USER_LATITUDE = "lat"
    const val APP_JSON = "application/json"
    val USER_LONGITUDE = "longi"
    val LOGIN_INFO = "login_info"
    val ARROW = "=>"
    val ERROR_CODE = -1
    val STATUS_ERROR_CODE = 5001
    val STATUS_SUCCESS_CODE = 5002
    val STATUS_SUCCESS_EXISTS_CODE = 5003
    val STATUS_SUCCESS_NOT_EXISTS_CODE = 5004
    val STATUS_SUCCESS_EMPTY_LIST_CODE = 5005
    val UTC = "UTC"

    val IS_SYNCING_START = "IS_SYNCING_START"
    val IS_SYNCING_STOP = "IS_SYNCING_STOP"

    val CONNECTIVITY_CHANGE = "CONNECTIVITY_CHANGE"

    val HOME_PLAN_TYPE_WORK_OUT = "workout"
    val HOME_PLAN_TYPE_TRAINING = "training"
    val HOME_PLAN_TYPE_PLAN = "plan"

    val PREF_IS_INSTRUCTION_SOUND_ON ="pref_is_instruction_sound_on"
    val PREF_IS_COACH_SOUND_ON ="pref_is_coach_sound_on"
    val PREF_IS_SOUND_MUTE ="pref_is_sound_mute"
    val PREF_IS_MUSIC_MUTE ="pref_is_music_mute"
    val PREF_IS_MUSIC_REPEAT ="pref_is_music_repeat"
    val PREF_IS_MUSIC_SHUFFLE ="pref_is_music_shuffle"
    val PREF_REST_TIME ="pref_rest_time"
    val PREF_READY_TO_GO_TIME ="pref_ready_to_go_time"
    val PREF_IS_FIRST_TIME ="pref_is_first_time"
    val PREF_WHATS_YOUR_GOAL ="pref_whats_your_goal"
    val PREF_IS_REMINDER_SET ="pref_is_reminder_set"
    val PREF_IS_KEEP_SCREEN_ON ="pref_is_keep_screen_on"

    const val PREF_WEIGHT_UNIT = "PREF_WEIGHT_UNIT"
    const val PREF_HEIGHT_UNIT = "PREF_HEIGHT_UNIT"
    const val PREF_LAST_INPUT_WEIGHT = "PREF_LAST_INPUT_WEIGHT"
    const val PREF_LAST_INPUT_FOOT = "PREF_LAST_INPUT_FOOT"
    const val PREF_LAST_INPUT_INCH = "PREF_LAST_INPUT_INCH"
    const val PREF_DOB = "PREF_DOB"
    const val PREF_IS_MUSIC_SELECTED = "PREF_IS_MUSIC_SELECTED"
    const val PREF_MUSIC = "PREF_MUSIC"
    const val PREF_GENDER = "PREF_GENDER"
    const val PREF_IS_WEEK_GOAL_DAYS_SET = "PREF_IS_WEEK_GOAL_DAYS_SET"
    const val PREF_WEEK_GOAL_DAYS = "PREF_WEEK_GOAL_DAYS"
    const val PREF_FIRST_DAY_OF_WEEK = "PREF_FIRST_DAY_OF_WEEK"
    const val PREF_RANDOM_DISCOVER_PLAN = "PREF_RANDOM_DISCOVER_PLAN"
    const val PREF_RANDOM_DISCOVER_PLAN_DATE = "PREF_RANDOM_DISCOVER_PLAN_DATE"

    const val PREF_KEY_PURCHASE_STATUS = "KeyPurchaseStatus"

    const val PlanTypeWorkout = "Workouts"

    const val PlanLvlTitle = "Title"
    const val PlanLvlBeginner = "Beginner"
    const val PlanLvlIntermediate = "Intermediate"
    const val PlanLvlAdvanced = "Advanced"

    const val PlanTypeWorkouts = "Workouts"
    const val PlanTypeSubPlan = "SubPlan"
    const val PlanTypeMyTraining = "MyTraining"

    const val MyTrainingThumbnail = "icon_thumbnail_my_training"
    const val MyTrainingTypeImage = "ic_type_my_training"

    const val PlanDaysYes = "YES"
    const val PlanDaysNo = "NO"

    const val extra_day_id = "extra_day_id"
    const val workout_type_step = "s"

    const val WORKOUT_TIME_FORMAT = "mm:ss"

    const val SEC_DURATION_CAL = 0.08
    const val DEFAULT_REST_TIME = 15L
    const val DEFAULT_READY_TO_GO_TIME = 15L
    const val CapDateFormatDisplay = "yyyy-MM-dd HH:mm:ss"

    const val extraReminderId = "extraReminderId"

    const val DEF_KG = "KG"
    const val DEF_LB = "LB"
    const val DEF_IN = "IN"
    const val DEF_FT = "FT"
    const val DEF_CM = "CM"

    const val MALE = "Male"
    const val FEMALE = "Female"

    const val WEIGHT_TABLE_DATE_FORMAT = "yyyy-MM-dd"
    const val DATE_TIME_24_FORMAT = "yyyy-MM-dd HH:mm:ss"
    const val DATE_FORMAT = "yyyy-MM-dd"

    const val Discover_Pain_Relief = "Pain Relief"
    const val Discover_Training = "Training"
    const val Discover_Flexibility = "Flexibility"
    const val Discover_ForBeginner = "ForBeginner"
    const val Discover_PostureCorrection = "PostureCorrection"
    const val Discover_FatBurning = "FatBurning"
    const val Discover_BodyFocus = "BodyFocus"
    const val Discover_Duration = "Duration"

    const val FROM_SETTING = "from setting"

    const val RESPONSE_FAILURE_CODE = 901
    const val RESPONSE_SUCCESS_CODE = 200
    const val VALIDATION_FAILED_CODE = 903
    const val USER_NOT_FOUND = 333

    fun getSuccessCode(): Int {
        return RESPONSE_SUCCESS_CODE
    }

    fun getFailureCode(): Int {
        return RESPONSE_FAILURE_CODE
    }

    fun getUserNotFoundCode(): Int {
        return USER_NOT_FOUND
    }

    var FirstTimeClickCount = 0
    var SecondTimeClickCount = 0


    const val ENABLE = "Enable"
    const val DISABLE = "Disable"

//    var BANNER = ""
//    var INTERSTITIAL = ""
//    var REWARD_VIDEO = ""

    /*New Key*/
    var FB_BANNER_TYPE_AD = "FB_BANNER_TYPE_AD"
    var FB_RECTANGLE_BANNER_TYPE_AD = "FB_RECTANGLE_BANNER_TYPE_AD"
    var GOOGLE_BANNER_TYPE_AD = "GOOGLE_BANNER_TYPE_AD"
    var GOOGLE_RECTANGLE_BANNER_TYPE_AD = "GOOGLE_RECTANGLE_BANNER_TYPE_AD"


    var AD_FACEBOOK = "facebook"
    var AD_GOOGLE = "google"

    var AD_TYPE_FB_GOOGLE = "facebook"

    var GOOGLE_BANNER = "ca-app-pub-3940256099942544/6300978111"
    var GOOGLE_INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"
    var GOOGLE_REWARDED_VIDEO = "ca-app-pub-3940256099942544/5224354917"

    var FB_BANNER = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID"
    var FB_BANNER_MEDIUM_RECTANGLE = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID"
    var FB_INTERSTITIAL = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID"

    var FIRST_CLICK_COUNT = 5


    const val  EXTRA_REMINDER_ID: String = "Reminder_ID"


}