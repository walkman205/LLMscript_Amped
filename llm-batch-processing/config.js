// config.js
export const API_CONFIG = {
  useAnthropic: false, // Set to true to use the Anthropic API
  useGemini: false, // Set to true to use the Google Gemini API
  useGPT4Turbo: false,
  useGPT4o: false,
  useO1Mini: false,
  useO1preview: true,
  useLlamaAPI: false,
  //llamaAPImodel: "mixtral8x7b-instruct",
  //llamaAPImodel: "llama3.3-70b",
  //llamaAPImodel: "deepseek-v3",
  llamaAPImodel: "deepseek-r1",
  useLocal: false,
  //localModelName: "deepseek-r1-distill-llama-8b",
  //localModelName: "deepseek-r1-distill-llama-8b",
  localModelName: "deepseek-r1-distill-qwen-32b",
  //localModelName: "deepseek-r1-distill-llama-8b"
};

export const LOCAL_FILES = {
  inputDir: "items",
  outputDir: "output5Feb2025_preview"
}

export const PROMPT_CONFIG = {
  ZSP: false,
  few_shot_snippet: false,
  COT_verdictfirst_snippet: false,
  COT_verdictlast_snippet: true
};

export const GDRIVE_CONFIG = {
  // 30JAN2025
  CHOPS_ITEMS_URL: "https://docs.google.com/spreadsheets/d/1qauXqhclWTUSC8N1_ydBJ7z4QXbeiZtoX9rATp1M0As/export?format=csv&gid=0",
  // All items: "https://docs.google.com/spreadsheets/d/1znkPMnx9LuOMmI5B209qsaE4sDggeEKMoALSI6UmWnc/export?format=csv&gid=0",
  // MISSING items for rerun: "https://docs.google.com/spreadsheets/d/1qauXqhclWTUSC8N1_ydBJ7z4QXbeiZtoX9rATp1M0As/export?format=csv&gid=0",
  // Yoav running DeepSeek: CHOPS_ITEMS_URL: "https://docs.google.com/spreadsheets/d/12g_qKJ0gpFT3ns8G1zk2F4pIiBFVVlwIsVBG2-5iJY4/export?format=csv&gid=0",
  // Test file: CHOPS_ITEMS_URL: "https://docs.google.com/spreadsheets/d/10X27hD6v5SP5EvbyYb2DE1IG49HMVjOUvAqwHQhfUo0/export?format=csv&gid=0",
  PROMPT_SNIPPETS_URL: "https://docs.google.com/spreadsheets/d/1V8urpdL8ZhCYD5PvHmFwwZYRsC-T9Dgj_mwSPHZmeBg/export?format=csv&gid=0"
};


