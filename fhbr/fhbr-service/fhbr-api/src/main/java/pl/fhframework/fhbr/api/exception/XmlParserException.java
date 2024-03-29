/*
 * Copyright 2019 Asseco Poland S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file,
 * or at: http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package pl.fhframework.fhbr.api.exception;




public class XmlParserException extends ValidationException {

	public XmlParserException(String messageKey, String message, Throwable t, String... argArray) {
		super(messageKey, message, t, argArray);
		// TODO Auto-generated constructor stub
	}

	public XmlParserException(String message, String... argArray) {
		super(message, argArray);
		// TODO Auto-generated constructor stub
	}

	public XmlParserException(String message, Throwable t, String... argArray) {
		super(message, t, argArray);
		// TODO Auto-generated constructor stub
	}

}
