/// <reference path='../lang/Random.ts'/>
module com.noctarius.core.model {
    export class Identifier {
        private static random:lang.Random = new lang.Random();

        private _data:Uint8Array;

        constructor(data:Uint8Array) {
            this._data = data;
        }

        public equals(other:any):boolean {
            if (other instanceof Identifier) {
                var od:Uint8Array = other._data;
                if (od.length != this._data.length) {
                    return false;
                }
                return !(this._data < od || od < this._data);
            }
            return false;
        }

        public static fromBytes(data:Uint8Array):Identifier {
            return new Identifier(data);
        }

        public static randomIdentifier():Identifier {
            var data:Uint8Array = new Uint8Array(16);
            Identifier.random.random(data);
            return new Identifier(data);
        }
    }
}