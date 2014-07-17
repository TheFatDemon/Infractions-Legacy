/*
 * Copyright 2014 Alexander Chauncey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.censoredsoftware.infractions.bukkit.legacy.util;

import com.rosaloves.bitlyj.Bitly.Provider;
import com.rosaloves.bitlyj.Url;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static com.rosaloves.bitlyj.Bitly.as;
import static com.rosaloves.bitlyj.Bitly.shorten;

public class URLUtil {
    /**
     * Converts a URL into a bit.ly shortened URL.
     *
     * @return String
     */
    public static String convertURL(String input) {
        if (!SettingUtil.getSettingBoolean("bitly.use")) return input;
        if (!input.startsWith("http://") && !input.startsWith("https://")) input = ("http://" + input);
        Provider bitly = as(SettingUtil.getSettingString("bitly.user"), SettingUtil.getSettingString("bitly.key"));
        Url shortUrl = bitly.call(shorten(input));
        return shortUrl.getShortUrl();
    }

    public static boolean isValidURL(String input) {
        if (!input.startsWith("http://") && !input.startsWith("https://")) {
            input = ("http://" + input);
        }
        try {
            URI uri = new URI(input);
            URL url = uri.toURL();
            java.net.URLConnection conn = url.openConnection();
            conn.connect();
            return true;
        } catch (MalformedURLException e) {
            return false;
        } catch (IOException e) {
            return false;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
