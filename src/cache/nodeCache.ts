import NodeCache from "node-cache";
import config from "config";

class CacheService {
  private static instance: CacheService;
  private cache: NodeCache;

  private constructor() {
    this.cache = new NodeCache({
      stdTTL: config.get("node-cache.stdTTL"),
      checkperiod: config.get("node-cache.checkperiod"),
      useClones: config.get("node-cache.useClones"),
      deleteOnExpire: config.get("node-cache.deleteOnExpire"),
    }); 
  }

  public static getInstance(): CacheService {
    if (!CacheService.instance) {
      CacheService.instance = new CacheService();
    }
    return CacheService.instance;
  }

  public getCache(): NodeCache {
    return this.cache;
  }
}

export default CacheService.getInstance();
