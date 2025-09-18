package com.eviort.cabedriver.NTHelper

object URLHelper {

    const val base = "https://www.cab-e.com/"
    //"https://cabe.uatdemo.in/"
    // https://cabe.appdemo.in/, ""https://www.cab-e.com/"

    const val client_id = 2
    const val client_secret = "a14HjS0NByjwNPZFJ4eHCJW0iEA1Uzju2pRDT7fo"

    const val HELP_URL = base
    const val REDIRECT_SHARE_URL = "http://maps.google.com/maps?q=loc:"
    const val LOGIN_WITH_OTP = base + "api/provider/mobile"
    const val SIGNUP_WITH_OTP = base + "api/provider/send_otp"
    const val WALLET_HISTORY = base + "api/provider/provider-wallet-history"
    const val VERIFYOTP = base + "api/provider/otp/verify"
    const val STREETRIDE_FARE = base + "api/provider/streetride/fare"
    const val STREETRIDE_TRIP_START = base + "api/provider/streetride/request"
    const val UPDATE_DEST = base + "api/provider/update/destination"
    const val DRIVER_DOCUMENT_URL = base + "provider/login"
    const val TERMS_URL = base + "terms-conditions"
    const val POLICY_URL = base
    val APP_URL = "https://play.google.com/store/apps/details?id=com.eviort.cabedriver&hl=en"
    val DRIVER_APP_URL = "https://play.google.com/store/apps/details?id=com.eviort.cabedriver&hl=en"
    val CUSTOMER_APP_URL = "https://play.google.com/store/apps/details?id=com.eviort.cabeuser"

    //POST METHOD
    const val GET_OAUTH_TOKEN = base + "api/provider/oauth/token"
    const val GET_CASHOUT_HISTORY = base + "api/provider/cashout/list"

    //POST METHOD
    const val LOGIN = base + "api/provider/oauth/token"

    const val CASHOUT = base + "api/provider/cashout/request"

    //POST METHOD
    const val SIGNUP = base + "api/provider/register"

    //POST METHOD
    const val LOGOUT = base + "api/provider/logout"
    var UPDATE_AVAILABILITY_API = base + "api/provider/profile/available"

    //POST METHOD
    const val RESET_PASSWORD = base + "api/provider/reset/password"

    //POST METHOD
    const val PROFILE_CHANGE_PASSWORD = base + "api/provider/profile/password"

    //POST METHOD
    const val FORGET_PASSWORD = base + "api/provider/forgot/password"

    //POST METHOD
    const val REFRESH_TOKEN = base + "api/provider/refresh/token"

    //POST METHOD
    const val DRIVER_STATUS = base + "api/provider/profile/available"

    //GET METHOD
    const val GET_PROFILE_DETAILS = base + "api/provider/profile"


    //GET METHOD
    const val GET_MAP_KEY = base + "api/provider/mapkey"

    //POST METHOD
    const val PROFILE_UPDATE = base + "api/provider/profile"

    //POST METHOD
    const val UPDATE_LOCATION = base + "api/provider/profile/location"

    //POST METHOD
    const val UPDATE_LOCATION_GET_STATUS = base + "api/provider/requests/status"

    const val CLEAR_STATUS = base + "api/provider/requests/clear/status?"

    //GET METHOD
    const val OFFER_LIST = base + "api/provider/requests/offered"

    //POST METHOD
    const val OFFER_DETAIL = base + "api/provider/requests/offered/"

    //GET METHOD
    const val PLANNED_LIST = base + "api/provider/requests/planned"

    //POST METHOD
    const val PLANNED_DETAIL = base + "api/provider/requests/planned/"

    //GET METHOD
    const val SEARCHING_DETAIL = base + "api/provider/requests/searching/detail"

    //GET METHOD
    const val REJECT = base + "api/provider/requests/destroy/"

    //POST METHOD
    const val CANCEL_TRIP = base + "api/provider/requests/cancel"

    //POST METHOD
    const val ACCEPTED = base + "api/provider/requests/accept/"

    //GET METHOD
    const val ACCEPTED_DETAIL = base + "api/provider/requests/accept/detail"

    //POST METHOD
    const val STARTED = base + "api/provider/requests/started/"

    //GET METHOD
    const val STARTED_DETAIL = base + "api/provider/requests/started/detail"

    //POST METHOD
    const val ARRIVED = base + "api/provider/requests/arrived/"

    //GET METHOD
    const val ARRIVED_DETAIL = base + "api/provider/requests/arrived/detail"

    //POST METHOD
    const val PICKEDUP = base + "api/provider/requests/pickedup/"

    //GET METHOD
    const val PICKEDUP_DETAIL = base + "api/provider/requests/pickedup/detail"

    //POST METHOD
    const val DROPPED = base + "api/provider/requests/dropped/"

    //GET METHOD
    const val DROPPED_DETAIL = base + "api/provider/requests/dropped/detail"

    //POST METHOD
    const val END = base + "api/provider/requests/end/"

    //POST METHOD
    const val CONFIRM_PAYMENT = base + "api/provider/payment/update"

    //GET METHOD
    const val END_DETAIL = base + "api/provider/requests/end/detail"

    //GET METHOD
    const val COMPLETED_LIST = base + "api/provider/requests/completed"

    //POST METHOD
    const val COMPLETED = base + "api/provider/requests/completed/"

    //GET METHOD
    const val COMPLETED_DETAIL = base + "api/provider/requests/completed/detail"

    //POST METHOD
    const val RATING = base + "api/provider/requests/rate/"

    //GET METHOD
    const val HELP = base + "api/provider/help"

    //GET METHOD
    const val PAST_TRIP_LIST = base + "api/provider/requests/past/trips"

    //POST METHOD
    const val PAST_TRIP_DETAIL = base + "api/provider/requests/past/detail/"
    const val UPCOMING_TRIP_DETAILS = base + "api/provider/requests/scheduled/"
    const val SUMMARY = base + "api/provider/summary"
    const val RESEND_OTP = base + "api/provider/resend"
    const val GET_NOTIFICATION = base + "api/provider/pushNotification?id="
    const val DOWNLOAD_DOCUMENT = base + "api/provider/document/download"
    const val UPLOAD_DOCUMENT = base + "api/provider/profile/upload"
    const val DELETE_DOCUMENT = base + "api/provider/profile/delete"
    const val GET_DOCUMENT = base + "api/provider/profile/getdocuments"

    //POST METHOD
    const val UPDATE_LOCATION_TO_SERVER = base + "api/provider/gps"

    //GET METHOD
    const val REQUESTED_SCHEDULED = base + "api/provider/requests/scheduled"

    //POST METHOD
    const val REQUESTED_SCHEDULED_DETAILS = base + "api/provider/requests/scheduled"

    //POST METHOD
    const val UPDATE_ADDITIONAL_FARE = base + "api/provider/additional/fare"
    const val EARNINGS = base + "api/provider/earnings"
    const val EARNINGS_DETAIL = base + "api/provider/earning/details"
    const val GET_PAST_HISTORY = base + "api/provider/requests/all/trips"
    const val GET_PAST_TRIP = base + "api/provider/reassign"
    const val GET_INVOICE = base + "api/provider/invoice-copy"
    const val GET_TODAY_HISTORY = base + "api/provider/requests/completed"

    //POST METHOD
    const val EMERGENCY_CONTACT_ADD = base + "api/provider/contact/add"

    //POST METHOD
    const val EMERGENCY_CONTACT_LIST = base + "api/provider/contact/list"

    //POST METHOD
    const val EMERGENCY_CONTACT_DELETE = base + "api/provider/contact/delete"

    //POST METHOD
    const val DELETE_ACCOUNT = base + "api/provider/delete"
}