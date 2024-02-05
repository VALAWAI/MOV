export const environment = {
  production: true,
  movUrl: (window as { [key: string]: any })["env"]["movUrl"] || "http://mov.valawai.eu"
};
