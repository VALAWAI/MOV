export const environment = {
  production: true,
  movApiUrl: (window as { [key: string]: any })["env"]["movApiUrl"] || "http://mov.valawai.eu/api",
  movUiUrl: (window as { [key: string]: any })["env"]["movUiUrl"] || "http://mov.valawai.eu/ui",
  lang: (window as { [key: string]: any })["env"]["lang"] || "en",
  version: (window as { [key: string]: any })["env"]["version"] || "undefined",
  buildTime: (window as { [key: string]: any })["env"]["buildTime"] || "0",
  startTime: (window as { [key: string]: any })["env"]["startTime"] || "0"
};
