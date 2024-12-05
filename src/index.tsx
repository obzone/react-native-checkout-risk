import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-checkout-risk' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const CheckoutRisk = NativeModules.CheckoutRisk
  ? NativeModules.CheckoutRisk
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function multiply(a: number, b: number): Promise<number> {
  return CheckoutRisk.multiply(a, b);
}

export async function initialize(publicKey: string, environment: string) {
  return CheckoutRisk.initialize(publicKey, environment);
}

export async function publishData() {
  return CheckoutRisk.publishData();
}
