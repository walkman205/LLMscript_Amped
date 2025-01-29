// config.js
export const API_CONFIG = {
  useAnthropic: true, // Set to true to use the Anthropic API
  useGemini: true, // Set to true to use the Google Gemini API
  useGPT4Turbo: false,
  useGPT4o: false,
  useO1Mini: true,
  useLLAMA: true,
  inputDir: "items",
  outputDir: "output",
  ZSP: true,
  few_shot_snippet: false,
  COT_verdictfirst_snippet: false,
  COT_verdictlast_snippet: false,
};
