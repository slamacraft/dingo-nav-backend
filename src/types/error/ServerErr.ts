import {HttpStatusCode} from "axios";

export class ServerErr extends Error {
  errCode: number = HttpStatusCode.InternalServerError;
  override message: string = "Internal Server Error!";

  constructor(msg?: string | Error, code?: number) {
    super();
    if (msg instanceof Error) {
      this.message = msg.message;
    } else {
      this.message = msg;
    }
    if (code) {
      this.errCode = code;
    }
  }
}
