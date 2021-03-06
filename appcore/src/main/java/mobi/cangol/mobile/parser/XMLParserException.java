/**
 * Copyright (c) 2013 Cangol
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.parser;

import org.w3c.dom.Node;

import java.lang.reflect.Field;

public class XMLParserException extends ParserException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Node mNode;

    public XMLParserException(Throwable throwable) {
        super(throwable);
        mNode = null;
    }

    public XMLParserException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public XMLParserException(Class<?> clazz, String message, Throwable throwable) {
        super(clazz, message, throwable);
    }

    public XMLParserException(Class<?> clazz, Field field, String message, Throwable throwable) {
        super(clazz, field, message, throwable);
    }

    public XMLParserException(Node node, String message, Throwable throwable) {
        super(message, throwable);
        mNode = node;
    }

    @Override
    public String getMessage() {
        if (mNode != null) {
            if (mNode.getTextContent() != null) {
                return "\nError '" + super.getMessage() + "' occurred in \n" + mNode.getTextContent() + "\n" + getCause().getMessage();
            } else {
                return "\nError '" + super.getMessage() + "' occurred in " + mNode.getNodeName() + "\n" + getCause().getMessage();
            }
        } else {
            return super.getMessage();
        }
    }

}
