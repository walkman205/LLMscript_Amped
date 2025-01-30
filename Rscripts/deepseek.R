library(dplyr)
library(irr)    # for kappa2

ds32 <- read.csv("/Users/yoavbergner/Documents/GitHub/LLMscript_Amped/llm-batch-processing/summary_deepseek32b.csv")
ds70 <- read.csv("/Users/yoavbergner/Documents/GitHub/LLMscript_Amped/llm-batch-processing/summary_deepseek70b.csv")

compute_metrics <- function(df_subset) {
  rand_accuracy <- mean(df_subset$ground_truth == df_subset$output_verdict)
  kappa_value <- kappa2(df_subset[,c("ground_truth", "output_verdict")])$value
  
  # Return a data frame (or tibble) of results
  tibble(
    rand_accuracy = rand_accuracy,
    cohen_kappa   = kappa_value
  )
}

compute_metrics(ds32)
ds32 %>%
  group_by(item_type) %>%
  do(compute_metrics(.)) %>%
  ungroup()

compute_metrics(ds70)
ds70 %>%
  group_by(item_type) %>%
  do(compute_metrics(.)) %>%
  ungroup()
