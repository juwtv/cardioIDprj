package com.example.segundatc.androidAuto.auth

import android.annotation.SuppressLint
import android.text.SpannableStringBuilder
import androidx.activity.OnBackPressedCallback
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.InputCallback
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template
import androidx.car.app.model.signin.InputSignInMethod
import androidx.car.app.model.signin.SignInTemplate
import androidx.car.app.versioning.CarAppApiLevels
import com.example.shared.R

class CreateDriverScreen(carContext: CarContext) : Screen(carContext) {
    companion object {
        private const val EMAIL_REGEXP = "^(.+)@(.+)$"
        private const val EXPECTED_PASSWORD = "password"
        private const val MIN_USERNAME_LENGTH = 5
    }

    private val mAdditionalText: CharSequence
    private val userPreferences = UserPreferences(carContext)

    // package private to avoid synthetic accessor
    var mState: State = State.USERNAME
    var mLastErrorMessage: String? = null // last displayed error message
    var mErrorMessage: String? = null
    var mEmail: String? = null
    var mUsername: String? = null

    init {
        // Handle back pressed events manually, as we use them to navigate between templates within
        // the same screen.
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mErrorMessage = ""
                when (mState) {
                    State.USERNAME, State.EMAIL, State.SIGNED_IN -> screenManager.pop()
                    else -> {
                        mState = State.entries.toTypedArray()[mState.ordinal - 1]
                        invalidate()
                    }
                }
            }
        }
        carContext.onBackPressedDispatcher.addCallback(this, callback)

        val additionalText = SpannableStringBuilder(getCarContext().getString(R.string.additional))
        mAdditionalText = additionalText
    }

    override fun onGetTemplate(): Template {
        if (carContext.carAppApiLevel < CarAppApiLevels.LEVEL_2) {
            return MessageTemplate.Builder(
                carContext.getString(R.string.sign_in_template_not_supported_txt)
            )
                .setTitle(carContext.getString(R.string.sign_in_template_not_supported_tit))
                .setHeaderAction(Action.BACK)
                .build()
        }
        return when (mState) {
            State.USERNAME -> getUsernameSignInTemplate()
            State.EMAIL -> getEmailSignInTemplate()
            State.PASSWORD -> getPasswordSignInTemplate()
            State.SIGNED_IN -> getSignInCompletedMessageTemplate()
        }
    }

    private fun getUsernameSignInTemplate(): Template {
        val listener = object : InputCallback {
            override fun onInputSubmitted(text: String) {
                if (mState == State.USERNAME) {
                    mUsername = text
                    submitUsername()
                }
            }

            override fun onInputTextChanged(text: String) {
                if (mState == State.USERNAME) {
                    mUsername = text
                    mErrorMessage = validateUsername()

                    if (!mLastErrorMessage.isNullOrEmpty() &&
                        (mErrorMessage.isNullOrEmpty() || mLastErrorMessage != mErrorMessage)
                    ) {
                        invalidate()
                    }
                }
            }
        }

        val builder = InputSignInMethod.Builder(listener)
            .setHint(carContext.getString(R.string.username_ht))
            .setKeyboardType(InputSignInMethod.KEYBOARD_DEFAULT)
        if (mErrorMessage != null) {
            builder.setErrorMessage(mErrorMessage!!)
            mLastErrorMessage = mErrorMessage
        }
        if (mUsername != null) {
            builder.setDefaultValue(mUsername!!)
        }
        val signInMethod = builder.build()

        return SignInTemplate.Builder(signInMethod)
            .setTitle(carContext.getString(R.string.sign_in_tit))
            .setInstructions(carContext.getString(R.string.username_instr))
            .setHeaderAction(Action.BACK)
            .setAdditionalText(mAdditionalText)
            .build()
    }

    private fun getEmailSignInTemplate(): Template {
        val listener = object : InputCallback {
            override fun onInputSubmitted(text: String) {
                if (mState == State.EMAIL) {
                    mEmail = text
                    submitEmail()
                }
            }

            override fun onInputTextChanged(text: String) {
                if (mState == State.EMAIL) {
                    mEmail = text
                    mErrorMessage = validateEmail()

                    if (!mLastErrorMessage.isNullOrEmpty() &&
                        (mErrorMessage.isNullOrEmpty() || mLastErrorMessage != mErrorMessage)
                    ) {
                        invalidate()
                    }
                }
            }
        }

        val builder = InputSignInMethod.Builder(listener)
            .setHint(carContext.getString(R.string.email_ht))
            .setKeyboardType(InputSignInMethod.KEYBOARD_EMAIL)
        if (mErrorMessage != null) {
            builder.setErrorMessage(mErrorMessage!!)
            mLastErrorMessage = mErrorMessage
        }
        if (mEmail != null) {
            builder.setDefaultValue(mEmail!!)
        }
        val signInMethod = builder.build()

        return SignInTemplate.Builder(signInMethod)
            .setTitle(carContext.getString(R.string.sign_in_tit))
            .setInstructions(carContext.getString(R.string.email_instr))
            .setHeaderAction(Action.BACK)
            //.setAdditionalText(mAdditionalText)
            .build()
    }

    @SuppressLint("StringFormatInvalid")
    fun validateUsername(): String {
        return when {
            mUsername.isNullOrEmpty() || mUsername!!.length < MIN_USERNAME_LENGTH -> {
                carContext.getString(R.string.invalid_length_error_msg, MIN_USERNAME_LENGTH.toString())
            }
            else -> ""
        }
    }

    fun submitUsername() {
        mErrorMessage = validateUsername()

        val isError = !mErrorMessage.isNullOrEmpty()
        if (!isError) {
            userPreferences.setCurrentDriverUUID(mUsername)
            mState = State.EMAIL
        }

        invalidate()
    }

    @SuppressLint("StringFormatInvalid")
    fun validateEmail(): String {
        return when {
            mEmail.isNullOrEmpty() || mEmail!!.length < MIN_USERNAME_LENGTH -> {
                carContext.getString(R.string.invalid_length_error_msg, MIN_USERNAME_LENGTH.toString())
            }
            !mEmail!!.matches(EMAIL_REGEXP.toRegex()) -> {
                carContext.getString(R.string.invalid_email_error_msg)
            }
            else -> ""
        }
    }

    fun submitEmail() {
        mErrorMessage = validateEmail()

        val isError = !mErrorMessage.isNullOrEmpty()
        if (!isError) {
            mState = State.PASSWORD
        }

        invalidate()
    }

    private fun getPasswordSignInTemplate(): Template {
        val callback = object : InputCallback {
            override fun onInputSubmitted(text: String) {
                mErrorMessage = if (EXPECTED_PASSWORD != text) {
                    carContext.getString(R.string.invalid_password_error_msg)
                } else {
                    mState = State.SIGNED_IN
                    ""
                }
                invalidate()
            }
        }

        val builder = InputSignInMethod.Builder(callback)
            .setHint(carContext.getString(R.string.password_ht))
            .setInputType(InputSignInMethod.INPUT_TYPE_PASSWORD)
        if (mErrorMessage != null) {
            builder.setErrorMessage(mErrorMessage!!)
        }
        val signInMethod = builder.build()

        return SignInTemplate.Builder(signInMethod)
            .setTitle(carContext.getString(R.string.sign_in_tit))
            .setInstructions(
                carContext.getString(R.string.password_sign_in_instr) + ": " + mEmail
            )
            .setHeaderAction(Action.BACK)
            //.setAdditionalText(mAdditionalText)
            .build()
    }

    private fun getSignInCompletedMessageTemplate(): MessageTemplate {
        return MessageTemplate.Builder(carContext.getString(R.string.sign_in_complete_txt))
            .setHeaderAction(Action.BACK)
            .setTitle(carContext.getString(R.string.sign_in_tit))
            .addAction(
                Action.Builder()
                    .setTitle(carContext.getString(R.string.start_auth))
                    .setOnClickListener {
                        // Adicionar o driver criado à lista de drivers
                        mUsername?.let { username ->
                            userPreferences.addDriver(username)
                        }
                        screenManager.push(AuthenticationScreen(carContext, true))
                    }
                    /*.setTitle(carContext.getString(R.string.sign_out_action_title))
                    .setOnClickListener {
                        mState = State.USERNAME
                        invalidate()
                    }*/
                    .build()
            )
            .build()
    }

    enum class State {
        USERNAME, EMAIL, PASSWORD, SIGNED_IN
    }
}
