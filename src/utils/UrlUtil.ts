export function getParamMap(str: string): Map<string, string> {
  return new Map(
    str.split("&").map((entry) => {
      let tuple = entry.split("=");
      return [tuple[0], tuple[1]];
    })
  );
}
