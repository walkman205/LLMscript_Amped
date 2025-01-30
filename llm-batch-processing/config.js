// config.js
export const API_CONFIG = {
  useAnthropic: false, // Set to true to use the Anthropic API
  useGemini: false, // Set to true to use the Google Gemini API
  useGPT4Turbo: false,
  useGPT4o: false,
  useO1Mini: false,
  useLLAMA: false,
  llamaVersion: "llama33",
  useLocal: true,
  localModelName: "deepseekLlama8b"};

export const LOCAL_FILES = {
  inputDir: "items",
  outputDir: "output"
}

export const PROMPT_CONFIG = {
  ZSP: true,
  few_shot_snippet: false,
  COT_verdictfirst_snippet: false,
  COT_verdictlast_snippet: false
};

export const GDRIVE_CONFIG = { 
  CHOPS_ITEMS_URL: "https://docs.google.com/spreadsheets/d/12g_qKJ0gpFT3ns8G1zk2F4pIiBFVVlwIsVBG2-5iJY4/export?format=csv&gid=0",
  //  CHOPS_ITEMS_URL: "https://docs.google.com/spreadsheets/d/10X27hD6v5SP5EvbyYb2DE1IG49HMVjOUvAqwHQhfUo0/export?format=csv&gid=0",
  PROMPT_SNIPPETS_URL: "https://docs.google.com/spreadsheets/d/1V8urpdL8ZhCYD5PvHmFwwZYRsC-T9Dgj_mwSPHZmeBg/export?format=csv&gid=0"
};


