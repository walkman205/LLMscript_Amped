import Papa from 'papaparse';
import fetch from 'node-fetch';
import { GDRIVE_CONFIG } from "./config.js";

/**
 * Fetch the CSV file from a public Google Sheets direct-download link.
 * @param {string} url - The direct-download CSV URL
 * @returns {Promise<string>} - CSV file content as plain text
 */
async function fetchCSVFile(url) {
    const response = await fetch(url);
    if (!response.ok) {
        throw new Error(`Failed to fetch CSV: ${response.status} ${response.statusText}`);
    }
    return await response.text();
}

/**
 * Parse CSV text into an array of objects using PapaParse.
 * @param {string} csvText - Raw CSV text
 * @param {string} delimiter - Delimiter used in the CSV file (default is a comma)
 * @returns {Promise<Array<Object>>} - Array of row objects with keys from the header row
 */
function parseCSV(csvText, delimiter = ",") {
    return new Promise((resolve, reject) => {
        Papa.parse(csvText, {
            header: true,
            delimiter, // Specify the delimiter if it's not a comma
            skipEmptyLines: true,
            complete: (results) => resolve(results.data),
            error: reject,
        });
    });
}

/**
 * Process the CHOPS_items.csv file.
 * Transforms each row into [ itemname, item_text, ground_truth ].
 * @param {string} url - The direct-download CSV URL
 * @returns {Promise<Array<Array<string>>>} - Transformed array of arrays
 */
async function processCHOPSItems(url) {
    const csvText = await fetchCSVFile(url);
    const rows = await parseCSV(csvText);

    // Transform into array of arrays
    const chopsItems = rows.map((row) => [
        row.item_name,
        row.item_text,
        row.ground_truth,
    ]);

    return chopsItems;
}

/**
 * Process the Prompt_snippets.csv file.
 * Transforms each row into [ snippet_name, snippet_text ].
 * @param {string} url - The direct-download CSV URL
 * @returns {Promise<Array<Array<string>>>} - Transformed array of arrays
 */
async function processPromptSnippets(url) {
    const csvText = await fetchCSVFile(url);
    const rows = await parseCSV(csvText);

    // Transform into array of arrays
    const promptSnippets = rows.map((row) => [
        row.snippet_name,
        row.snippet_text,
    ]);

    return promptSnippets;
}



const chopsItems = await processCHOPSItems(GDRIVE_CONFIG.CHOPS_ITEMS_URL);
export { chopsItems };

const promptSnippets = await processPromptSnippets(GDRIVE_CONFIG.PROMPT_SNIPPETS_URL);
const promptvarieties = {};

for (const [name, text] of promptSnippets) {
    promptvarieties[name] = text;
}
export { promptvarieties };


//const preprompt = `${promptvarieties.ZSP}${promptvarieties.end_of_prompt_snippet}`;
//console.log(preprompt);