/*
 * Copyright 2006-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus;

import com.consol.citrus.dsl.TestNGCitrusTestBuilder;
import com.consol.citrus.dsl.annotations.CitrusTest;
import com.consol.citrus.jms.endpoint.JmsEndpoint;
import com.consol.citrus.testng.CitrusParameters;
import com.consol.citrus.ws.message.SoapMessageHeaders;
import com.consol.citrus.ws.server.WebServiceServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 * @since 2.0.1
 */
@Test
public class NewsFeedParameterTest extends TestNGCitrusTestBuilder {

    @Autowired
    private JmsEndpoint newsJmsEndpoint;

    @Autowired
    private WebServiceServer newsSoapServer;

    @CitrusTest(name = "NewsFeed_DataProvider_Ok_Test")
    @CitrusParameters({ "message" })
    @Test(dataProvider = "citrusDataProvider")
    public void newsFeed_DataProvider_Ok_Test(String message) {
        send(newsJmsEndpoint)
                .payload("<nf:News xmlns:nf=\"http://citrusframework.org/schemas/samples/news\">" +
                            "<nf:Message>${message}</nf:Message>" +
                        "</nf:News>");

        receive(newsSoapServer)
                .payload("<nf:News xmlns:nf=\"http://citrusframework.org/schemas/samples/news\">" +
                            "<nf:Message>" + message + "</nf:Message>" +
                        "</nf:News>")
                .header(SoapMessageHeaders.SOAP_ACTION, "newsFeed");

        send(newsSoapServer)
                .header(SoapMessageHeaders.HTTP_STATUS_CODE, "200");
    }

    @Override
    protected Object[][] getParameterValues() {
        return new Object[][] {{ "Citrus rocks!" },
                               { "Citrus really rocks!" },
                               { "Citrus is awesome!" }};
    }
}
