package org.comppress.customnewsapi.service.article;

import org.comppress.customnewsapi.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ArticleServiceTests {

    @Autowired
    private ArticleService articleService;

    @Test
    void formatText(){
        // TODO
        String s = "some Text\\n";
        //assertEquals(articleService.formatText);
    }

    @Test
    void updatePaywallArticles(){
        String paywallYes = "\"isAccessibleForFree\": false,\"hasPart\": {\"@type\": \"WebPageElement\",\"isAccessibleForFree\": false,\"cssSelector\": \".paywall\"}";
        String paywallNo = "<div aria-label=\"grammarly-integration\" role=\"group\" tabindex=\"-1\" class=\"grammarly-desktop-integration\" data-content=\"{&quot;mode&quot;:&quot;full&quot;,&quot;isActive&quot;:true,&quot;isUserDisabled&quot;:false}\"></div>";
        assertFalse(articleService.checkIfArticleIsAccessibleWithoutPaywall(paywallYes));
        assertTrue(articleService.checkIfArticleIsAccessibleWithoutPaywall(paywallNo));
    }

}
