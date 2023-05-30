import jwt from "jsonwebtoken";
import config from "config";

export function sign(
  content: Object,
  cb: (error: Error | null, encoded: string | undefined) => void
) {
  jwt.sign(
    content,
    config.get("jwtSecret"),
    { expiresIn: config.get("jwtExpiration") },
    cb
  );
}
