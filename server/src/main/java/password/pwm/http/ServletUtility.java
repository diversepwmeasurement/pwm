/*
 * Password Management Servlets (PWM)
 * http://www.pwm-project.org
 *
 * Copyright (c) 2006-2009 Novell, Inc.
 * Copyright (c) 2009-2021 The PWM Project
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

package password.pwm.http;

import password.pwm.PwmConstants;
import password.pwm.config.AppConfig;
import password.pwm.data.ImmutableByteArray;
import password.pwm.error.ErrorInformation;
import password.pwm.error.PwmError;
import password.pwm.error.PwmUnrecoverableException;
import password.pwm.util.java.JavaHelper;
import password.pwm.util.java.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLConnection;
import java.util.List;

public final class ServletUtility
{
    private ServletUtility()
    {
    }

    public static String readRequestBodyAsString( final HttpServletRequest req, final int maxChars )
            throws IOException, PwmUnrecoverableException
    {
        final String value = JavaHelper.copyToString( req.getInputStream(), PwmConstants.DEFAULT_CHARSET, maxChars + 1 )
                .orElse( "" );

        if ( value.length() > maxChars )
        {
            final String msg = "input request body is to big, size=" + value.length() + ", max=" + maxChars;
            throw new PwmUnrecoverableException( new ErrorInformation( PwmError.ERROR_INTERNAL, msg ) );
        }
        return value;
    }


    public static String mimeTypeForUserPhoto(
            final AppConfig configuration,
            final ImmutableByteArray immutableByteArray
    )
            throws IOException, PwmUnrecoverableException
    {
        final List<String> permittedMimeTypes = configuration.permittedPhotoMimeTypes();

        final String mimeType = URLConnection.guessContentTypeFromStream( immutableByteArray.newByteArrayInputStream() );
        if ( !StringUtil.isEmpty( mimeType ) && permittedMimeTypes.contains( mimeType ) )
        {
            return mimeType;
        }
        final ErrorInformation errorInformation = new ErrorInformation( PwmError.ERROR_FILE_TYPE_INCORRECT, "unsupported mime type" );
        throw new PwmUnrecoverableException( errorInformation );
    }
}
