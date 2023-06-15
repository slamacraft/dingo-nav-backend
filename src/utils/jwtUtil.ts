import jwt from "jsonwebtoken";
import config from "config";
import nodeCache from "src/cache/nodeCache";

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

export function signAndCache(
  content: Object,
  cb: (error: Error | null, encoded: string | undefined) => void
) {
  const jwtExpiration: string | number = config.get("jwtExpiration");
  jwt.sign(
    content,
    config.get("jwtSecret"),
    { expiresIn: jwtExpiration },
    (error: Error | null, encoded: string | undefined) => {
      if (!error && encoded) {
        nodeCache.getCache().set(encoded, jwtExpiration, jwtExpiration);
      }
    }
  );
}
