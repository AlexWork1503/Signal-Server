/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.whispersystems.textsecuregcm.recaptcha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.whispersystems.textsecuregcm.recaptcha.EnterpriseRecaptchaClient.SEPARATOR;

import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.ws.rs.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class EnterpriseRecaptchaClientTest {

  private static final String SITE_KEY = "site-key";
  private static final String TOKEN = "some-token";

  @ParameterizedTest
  @MethodSource
  void parseInputToken(final String input, final String expectedToken, final String siteKey,
      @Nullable final String expectedAction) {

    final String[] parts = EnterpriseRecaptchaClient.parseInputToken(input);

    assertEquals(siteKey, parts[0]);
    assertEquals(expectedAction, parts[1]);
    assertEquals(expectedToken, parts[2]);
  }

  @Test
  void parseInputTokenBadRequest() {
    assertThrows(BadRequestException.class, () -> {
      EnterpriseRecaptchaClient.parseInputToken(TOKEN);
    });
  }

  static Stream<Arguments> parseInputToken() {
    return Stream.of(
        Arguments.of(
            String.join(SEPARATOR, SITE_KEY, TOKEN),
            TOKEN,
            SITE_KEY,
            null),
        Arguments.of(
            String.join(SEPARATOR, SITE_KEY, "an-action", TOKEN),
            TOKEN,
            SITE_KEY,
            "an-action"),
        Arguments.of(
            String.join(SEPARATOR, SITE_KEY, "an-action", TOKEN, "something-else"),
            TOKEN + SEPARATOR + "something-else",
            SITE_KEY,
            "an-action")
    );
  }
}
