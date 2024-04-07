package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.HarmCategory;
// import com.google.cloud.vertexai.api.ResponseStream;
import com.google.cloud.vertexai.api.SafetySetting;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MultiModalController {

    @GetMapping("/generateContent")
    public List<String> generateContent() throws IOException {
        try (VertexAI vertexAi = new VertexAI("striped-rhino-419317", "us-central1")) {
            GenerationConfig generationConfig =
                GenerationConfig.newBuilder()
                    .setMaxOutputTokens(256)
                    .setTemperature(0.2F)
                    .setTopP(0.95F)
                    .setTopK(40)
                    .build();
            List<SafetySetting> safetySettings = Arrays.asList(
                SafetySetting.newBuilder()
                    .setCategory(HarmCategory.HARM_CATEGORY_HATE_SPEECH)
                    .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
                    .build(),
                SafetySetting.newBuilder()
                    .setCategory(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
                    .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
                    .build(),
                SafetySetting.newBuilder()
                    .setCategory(HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT)
                    .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
                    .build(),
                SafetySetting.newBuilder()
                    .setCategory(HarmCategory.HARM_CATEGORY_HARASSMENT)
                    .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
                    .build()
            );
            GenerativeModel model =
                new GenerativeModel.Builder()
                    .setModelName("gemini-1.0-pro-001")
                    .setVertexAi(vertexAi)
                    .setGenerationConfig(generationConfig)
                    .setSafetySettings(safetySettings)
                    .build();


            var content = ContentMaker.fromMultiModalData("Article: Yellowstone National Park is an American national park located in the western United States, largely in the northwest corner of Wyoming and extending into Montana and Idaho. It was established by the 42nd U.S. Congress with the Yellowstone National Park Protection Act and signed into law by President Ulysses S. Grant on March 1, 1872. Yellowstone was the first national park in the U.S. and is also widely held to be the first national park in the world. The park is known for its wildlife and its many geothermal features, especially the Old Faithful geyser, one of its most popular. While it represents many types of biomes, the subalpine forest is the most abundant. It is part of the South Central Rockies forests ecoregion.\nThe title of above article can be: Yellowstone National Park: A Natural Wonder\n\nArticle: As many businesses figure out new ways to go digital, one thing is clear: talent continues to be one of the key ways to enable an inclusive digital economy. Employers in Asia Pacific list technology as the leading in-demand skill, with digital marketing and e-commerce following close behind. Simultaneously, many people are looking to learn new skills that will help them meet the requirements of the evolving job market. So we must create new ways to help businesses and job seekers alike.\nThe title of above article can be: How to Prepare for the Digital Economy\n\nArticle: STEM Minds empowers K-12 students worldwide to reach their full potential as self-directed, life-long learners. As we grow our team, we'll continue to work closely with Google for Startups experts and Google for Startups Accelerator Canada advisors to further expand our AI/ML tech stack, develop additional educational solutions, and launch STEM Minds in new markets.\nThe title of above article can be: STEM Minds: Empowering K-12 Students Worldwide\n\nArticle: As human beings, we learn from our personal experiences and from each other. We often share what we've learned and rework systems based on failures we've encountered. While our robots don't communicate with each other, this research shows that we can successfully combine datasets from different types of robots and transfer behaviors across them. In fact, our research shows that by combining data from different robots we're able to nearly double the model's ability to generalize to a new scene. That means that as we continue to experiment with different robots and new tasks, we may be able to augment the training data to improve robot behavior, making it a flexible and scalable approach to robot learning.\nThe title of above article can be:\n");
            com.google.cloud.vertexai.generativeai.ResponseStream<GenerateContentResponse> responseStream = model.generateContentStream(content);

            // Collect responses into a list of strings
            List<String> responses = new ArrayList<>();
            responseStream.stream().forEach(response -> responses.add(response.toString()));

            return responses;
        }
    }
}
