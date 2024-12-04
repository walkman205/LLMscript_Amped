// processFiles.js
import { API_CONFIG } from "./config.js";
import { preprompt } from "./preprompt.js";
import { getFiles, readFileContent, writeFileContent } from "./fileUtils.js";

import { generateContentAnthropic } from "./anthropicAPI.js";
import { generateContentGoogle } from "./googleAPI.js";
import { generateContentGPT4o } from "./openAI_GPT4o.js";
import { generateContentGPT4Turbo } from "./openAI_GPT4Turbo.js";

async function processFiles() {
  try {
    const allFiles = await getFiles(API_CONFIG.inputDir);
    // Filter the files to include only those ending with '.txt'
    const files = allFiles.filter((file) => file.endsWith(".txt"));

    for (const file of files) {
      const itemcontent = await readFileContent(API_CONFIG.inputDir, file);
      const content = preprompt + itemcontent;

      // Process with Anthropic API if enabled
      if (API_CONFIG.useAnthropic) {
        try {
          const generatedTextAnthropic = await generateContentAnthropic(
            content
          );
          const outputFileName = `output_anthropic_${file}`;
          await writeFileContent(
            API_CONFIG.outputDir,
            outputFileName,
            generatedTextAnthropic
          );
          console.log(
            `Processed ${file} with Anthropic API, output saved to ${API_CONFIG.outputDir}/${outputFileName}`
          );
        } catch (apiError) {
          console.error(
            `Error processing ${file} with Anthropic API:`,
            apiError
          );
        }
      }

      // Process with Google Gemini API if enabled
      if (API_CONFIG.useGemini) {
        try {
          const generatedTextGoogle = await generateContentGoogle(content);
          const outputFileName = `output_gemini_${file}`;
          await writeFileContent(
            API_CONFIG.outputDir,
            outputFileName,
            generatedTextGoogle
          );
          console.log(
            `Processed ${file} with Google Gemini API, output saved to ${API_CONFIG.outputDir}/${outputFileName}`
          );
        } catch (apiError) {
          console.error(
            `Error processing ${file} with Google Gemini API:`,
            apiError
          );
        }
      }

      // Process with OpenAI API if enabled
      if (API_CONFIG.useGPT4o) {
        try {
          const generatedTextOpenAI = await generateContentGPT4o(content);
          const outputFileName = `output_openai_${file}`;
          await writeFileContent(
            API_CONFIG.outputDir,
            outputFileName,
            generatedTextOpenAI
          );
          console.log(
            `Processed ${file} with OpenAI GPT4o, output saved to ${API_CONFIG.outputDir}/${outputFileName}`
          );
        } catch (apiError) {
          console.error(`Error processing ${file} with OpenAI GPT4o:`, apiError);
        }
      }


      // Process with OpenAI API if enabled
      if (API_CONFIG.useOpenAI) {
        try {
          const generatedTextOpenAI = await generateContentGPT4Turbo(content);
          const outputFileName = `output_openai_${file}`;
          await writeFileContent(
            API_CONFIG.outputDir,
            outputFileName,
            generatedTextOpenAI
          );
          console.log(
            `Processed ${file} with OpenAI GPT-4-Turbo, output saved to ${API_CONFIG.outputDir}/${outputFileName}`
          );
        } catch (apiError) {
          console.error(`Error processing ${file} with OpenAI GPT-4-Turbo:`, apiError);
        }
      }
    }
  } catch (error) {
    console.error("Error processing files:", error);
  }
}

processFiles();
