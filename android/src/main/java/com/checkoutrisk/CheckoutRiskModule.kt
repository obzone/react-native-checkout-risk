package com.checkoutrisk

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise

import com.checkout.risk.PublishDataResult
import com.checkout.risk.Risk
import com.checkout.risk.RiskConfig
import com.checkout.risk.RiskEnvironment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CheckoutRiskModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  private var riskInstance: Risk? = null

  override fun getName(): String {
    return NAME
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  fun multiply(a: Double, b: Double, promise: Promise) {
    promise.resolve(a * b)
  }

  @ReactMethod
  fun initialize(publicKey: String, environment: String, promise: Promise) {
    CoroutineScope(Dispatchers.IO).launch {
      try {
          val riskEnvironment = when (environment.lowercase()) {
              "qa" -> RiskEnvironment.QA
              "production" -> RiskEnvironment.PRODUCTION
              else -> throw IllegalArgumentException("Invalid environment: $environment")
          }

          val config = RiskConfig(publicKey, riskEnvironment, false)
          riskInstance = Risk.getInstance(reactApplicationContext, config)
          promise.resolve("Risk SDK initialized successfully")
      } catch (e: Exception) {
          promise.reject("INIT_ERROR", e.message, e)
      }
    }
  }

  @ReactMethod
  fun publishData(promise: Promise) {
    CoroutineScope(Dispatchers.IO).launch {
      try {
        val result = riskInstance?.publishData()

        if (result is PublishDataResult.Success) {
          val deviceSessionId = result.deviceSessionId
          promise.resolve(deviceSessionId) // Return the session ID to JavaScript
        } else {
          promise.reject("PublishDataError", "Failed to publish data.")
        }
      } catch (e: Exception) {
        promise.reject("PublishDataException", e.message, e)
      }
    }
  }

  companion object {
    const val NAME = "CheckoutRisk"
  }
}
