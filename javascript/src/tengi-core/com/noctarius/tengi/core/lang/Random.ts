module com.noctarius.core.lang {
    export class Random implements RandomProvider {
        private provider:RandomProvider;

        constructor() {
            this.provider = Random.provider();
        }

        private static provider():RandomProvider {
            if (window.hasOwnProperty("crypto")) {
                return new CryptoRandomProvider();
            } else if (window.msCrypto) {
                return new MsCryptoRandomProvider();
            }
            return new FakeRandomProvider();
        }

        random(array:Uint8Array):void {
            this.provider.random(array);
        }

        randomNumber():Number {
            return this.provider.randomNumber();
        }
    }

    export interface RandomProvider {
        random(array:Uint8Array):void;
        randomNumber():Number;
    }

    class CryptoRandomProvider implements RandomProvider {
        private crypto:RandomSource;

        constructor() {
            var w = <CryptoWindow> window;
            this.crypto = w.crypto;
        }

        random(array:Uint8Array):void {
            this.crypto.getRandomValues(array);
        }

        randomNumber():Number {
            var value:Float64Array = new Float64Array(1);
            this.crypto.getRandomValues(value);
            return value[0];
        }
    }

    class MsCryptoRandomProvider implements RandomProvider {
        random(array:Uint8Array):void {
            window.msCrypto.getRandomValues(array);
        }

        randomNumber():Number {
            var value:Float64Array = new Float64Array(1);
            window.msCrypto.getRandomValues(value);
            return value[0];
        }
    }

    class FakeRandomProvider implements RandomProvider {
        random(array:Uint8Array):void {
            for (var i = 0; i < array.length; i++) {
                array[i] = Math.floor(Math.random() * 255);
            }
        }

        randomNumber():Number {
            return Math.random();
        }
    }

    interface CryptoWindow extends Window {
        crypto: RandomSource;
    }
}
