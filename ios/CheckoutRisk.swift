import Foundation
import Risk // Import the SDK

@objc(CheckoutRisk)
class CheckoutRisk: NSObject {
  private var riskSDK: Risk?

  @objc(multiply:withB:withResolver:withRejecter:)
  func multiply(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
    resolve(a*b)
  }

  @objc
  func initialize(_ publicKey: String, environment: String, resolver: @escaping RCTPromiseResolveBlock, rejecter: @escaping RCTPromiseRejectBlock) {
      let riskEnvironment: RiskEnvironment = environment.lowercased() == "qa" ? .qa : .production
      let config = RiskConfig(publicKey: publicKey, environment: riskEnvironment)
      
      self.riskSDK = Risk(config: config)
      
      self.riskSDK?.configure { configurationResult in
          switch configurationResult {
          case .failure(let errorResponse):
              rejecter("CONFIG_ERROR", errorResponse.localizedDescription, nil)
          case .success():
              resolver("Risk SDK initialized successfully")
          }
      }
  }
  
  @objc
  func publishData(_ resolver: @escaping RCTPromiseResolveBlock, rejecter: @escaping RCTPromiseRejectBlock) {
      guard let riskSDK = self.riskSDK else {
          rejecter("NOT_INITIALIZED", "Risk SDK is not initialized", nil)
          return
      }
      
      riskSDK.publishData { result in
          switch result {
          case .success(let response):
              resolver(["deviceSessionId": response.deviceSessionId])
          case .failure(let errorResponse):
              rejecter("PUBLISH_ERROR", errorResponse.localizedDescription, nil)
          }
      }
  }
}