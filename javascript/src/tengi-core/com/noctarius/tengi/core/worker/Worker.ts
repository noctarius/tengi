/// <reference path='../model/Identifier.ts'/>
module com.noctarius.core.worker {
    export interface Task {
        send(value:any):void;
        addTransferListener(listener:TransferListener):model.Identifier;
    }

    export interface TransferListener {
        onTransfer(value:any):void;
    }

    class TaskImpl implements Task {

        private sendProvider:SendProvider;
        private worker:Worker;

        constructor() {
            this.worker = new Worker();
        }

        send(value:any):void {

        }

        addTransferListener(listener:TransferListener):model.Identifier {
            return null;
        }
    }

    interface SendProvider {
        postMessage(value:any):void;
    }

    class WebkitSendProvider implements SendProvider {

        private _worker:WebkitWorker;

        constructor(worker:Worker) {
            this._worker = <WebkitWorker> worker;
        }

        postMessage(value:any):void {
            this._worker.webkitPostMessage(value, [value]);
        }
    }

    interface WebkitWorker extends Worker {
        webkitPostMessage(value:any, transferObjects:any[]):void;
    }
}
