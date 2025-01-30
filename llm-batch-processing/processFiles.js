// processFiles.js
import path from "path";
import fs from "fs/promises";
import { API_CONFIG } from "./config.js";
import { getFiles, readFileContent, writeFileContent } from "./fileUtils.js";

import { generateContentAnthropic } from "./anthropicAPI.js";
import { generateContentGoogle } from "./googleAPI.js";
import { generateContentGPT4o } from "./openAI_GPT4o.js";
import { generateContentGPT4Turbo } from "./openAI_GPT4Turbo.js";
import {generateContentO1Mini} from "./openAI_O1mini.js";
import {generateContentLLAMA} from "./LLama_openAI_client.js";
import {chopsItems,promptvarieties} from "./read_drive_csv.js";
import { generateContentLocal } from './localAPIdeepseek.js';



async function Runpreprompt(preprompt,prompt_name) {
  for (const [item_name, item_text] of chopsItems) {
    const content = preprompt + item_text;
    // Process with Anthropic API if enabled
    if (API_CONFIG.useAnthropic) {
      try {
        const generatedTextAnthropic = await generateContentAnthropic(
            content
        );

        const prepromptDir = path.join(API_CONFIG.outputDir, prompt_name);
        const modeltDir = path.join(prepromptDir, "Anthropic");
        await fs.mkdir(modeltDir, { recursive: true });
        const outputFileName = `${item_name}_output_anthropic.txt`;
        const outputPath = path.join(modeltDir, outputFileName);
        await writeFileContent(
            modeltDir,
            outputFileName,
            generatedTextAnthropic
        );
        console.log(
            `Processed ${item_name} with Anthropic API, output saved to ${outputPath}`
        );
      } catch (apiError) {
        console.error(
            `Error processing ${item_name} with Anthropic API:`,
            apiError
        );
      }
    }

    // Process with Google Gemini API if enabled
    if (API_CONFIG.useGemini) {
      try {
        const generatedTextGoogle = await generateContentGoogle(content);
        const prepromptDir = path.join(API_CONFIG.outputDir, prompt_name);
        const modeltDir = path.join(prepromptDir, "Gemini");
        await fs.mkdir(modeltDir, { recursive: true });
        const outputFileName = `${item_name}_output_gemini.txt`;
        const outputPath = path.join(modeltDir, outputFileName);
        await writeFileContent(
            modeltDir,
            outputFileName,
            generatedTextGoogle
        );
        console.log(
            `Processed ${item_name} with Google Gemini API, output saved to ${outputPath}`
        );
      } catch (apiError) {
        console.error(
            `Error processing ${item_name} with Google Gemini API:`,
            apiError
        );
      }
    }

    // Process with OpenAI API if enabled
    if (API_CONFIG.useGPT4o) {
      try {
        const generatedTextOpenAI = await generateContentGPT4o(content);

        const prepromptDir = path.join(API_CONFIG.outputDir, prompt_name);
        const modeltDir = path.join(prepromptDir, "gpt4o");
        await fs.mkdir(modeltDir, { recursive: true });
        const outputFileName = `${item_name}_gpt4o.txt`;
        const outputPath = path.join(modeltDir, outputFileName);
        await writeFileContent(
            modeltDir,
            outputFileName,
            generatedTextOpenAI
        );
        console.log(
            `Processed ${item_name} with OpenAI GPT4o, output saved to ${outputPath}`
        );
      } catch (apiError) {
        console.error(`Error processing ${item_name} with OpenAI GPT4o:`, apiError);
      }
    }


    // Process with OpenAI API if enabled
    if (API_CONFIG.useGPT4Turbo) {
      try {
        const generatedTextOpenAI = await generateContentGPT4Turbo(content);

        const prepromptDir = path.join(API_CONFIG.outputDir, prompt_name);
        const modeltDir = path.join(prepromptDir, "gpt4Turbo");
        await fs.mkdir(modeltDir, { recursive: true });
        const outputFileName = `${item_name}_gpt4turbo.txt`;
        const outputPath = path.join(modeltDir, outputFileName);
        await writeFileContent(
            modeltDir,
            outputFileName,
            generatedTextOpenAI
        );
        console.log(
            `Processed ${item_name} with OpenAI GPT-4-Turbo, output saved to ${outputPath}`
        );
      } catch (apiError) {
        console.error(`Error processing ${item_name} with OpenAI GPT-4-Turbo:`, apiError);
      }
    }

    // Process with OpenAI API if enabled
    if (API_CONFIG.useO1Mini) {
      try {
        const generatedTextOpenAI = await generateContentO1Mini(content);

        const prepromptDir = path.join(API_CONFIG.outputDir, prompt_name);
        const modeltDir = path.join(prepromptDir, "o1mini");
        await fs.mkdir(modeltDir, { recursive: true });
        const outputFileName = `${item_name}_useO1Mini.txt`;
        const outputPath = path.join(modeltDir, outputFileName);

        await writeFileContent(modeltDir, outputFileName, generatedTextOpenAI);
        console.log(`Processed ${item_name} with OpenAI O1-mini, output saved to ${outputPath}`);
      } catch (apiError) {
        console.error(`Error processing ${item_name} with OpenAI O1-mini:`, apiError);
      }
    }


    // Local LLM
    if (API_CONFIG.useLocal) {
      try {
        const localText = await generateContentLocal(content);
        const prepromptDir = path.join(API_CONFIG.outputDir, prompt_name);
        const modeltDir = path.join(prepromptDir, "local");
        await fs.mkdir(modeltDir, { recursive: true });
        const outputFileName = `${item_name}_useLocal.txt`;
        const outputPath = path.join(modeltDir, outputFileName);

        await writeFileContent(modeltDir, outputFileName, localText);
        console.log(`Processed ${item_name} with locally hosted model, output saved to ${outputPath}`);
      } catch (apiError) {
        console.error(`Error processing ${item_name} with local model:`, apiError);
      }
    }
    
    // Process with LLAMA if enabled
    if (API_CONFIG.useLLAMA) {
      try {
        const generatedTextOpenAI = await generateContentLLAMA(content);

        const prepromptDir = path.join(API_CONFIG.outputDir, prompt_name);
        const modeltDir = path.join(prepromptDir, "llama");
        await fs.mkdir(modeltDir, { recursive: true });
        const outputFileName = `${item_name}_useLLAMA.txt`;
        const outputPath = path.join(modeltDir, outputFileName);

        await writeFileContent(modeltDir, outputFileName, generatedTextOpenAI);
        console.log(`Processed ${item_name} with LLAMA, output saved to ${outputPath}`);
      } catch (apiError) {
        console.error(`Error processing ${item_name} with LLAMA:`, apiError);
      }
    }
  }
}



if (API_CONFIG.ZSP) {
  const preprompt = `${promptvarieties.ZSP}${promptvarieties.end_of_prompt_snippet}`; // Concatenate preprompts
  await Runpreprompt(preprompt, 'ZSP'); // Pass the preprompt and name to the function
}

if (API_CONFIG.few_shot_snippet) {
  const preprompt = `${promptvarieties.ZSP}${promptvarieties.few_shot_snippet}${promptvarieties.end_of_prompt_snippet}`; // Concatenate preprompts
  await Runpreprompt(preprompt, 'few_shot'); // Pass the preprompt and name to the function
}

if (API_CONFIG.COT_verdictfirst_snippet) {
  const preprompt = `${promptvarieties.ZSP}${promptvarieties.COT_verdictfirst_snippet}${promptvarieties.end_of_prompt_snippet}`; // Concatenate preprompts
  await Runpreprompt(preprompt, 'COT_verdictfirst'); // Pass the preprompt and name to the function
}

if (API_CONFIG.COT_verdictlast_snippet) {
  const preprompt = `${promptvarieties.ZSP}${promptvarieties.COT_verdictlast_snippet}${promptvarieties.end_of_prompt_snippet}`; // Concatenate preprompts
  await Runpreprompt(preprompt, 'COT_verdictlast'); // Pass the preprompt and name to the function
}
