/*
 * Copyright 2013 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.parser.builder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.raml.parser.annotation.Key;
import org.raml.parser.annotation.Value;
import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.utils.ConvertUtils;
import org.raml.parser.utils.NodeUtils;
import org.raml.parser.utils.ReflectionUtils;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class PojoTupleBuilder extends DefaultTupleBuilder<ScalarNode, Node>
{

    private Class<?> pojoClass;
    private String fieldName;

    public PojoTupleBuilder(String fieldName, Class<?> pojoClass)
    {
        super(new DefaultScalarTupleHandler(fieldName));
        this.fieldName = fieldName;
        this.pojoClass = pojoClass;

    }

    public PojoTupleBuilder(Class<?> pojoClass)
    {
        this(null, pojoClass);
    }

    @Override
    protected Map<String, TupleBuilder<?, ?>> getBuilders()
    {
        if (super.getBuilders().isEmpty())
        {
            addBuildersFor(pojoClass);
        }
        return super.getBuilders();
    }

    @Override
    public Object buildValue(Object parent, Node node)
    {
        try
        {
            Object newValue;
            if (pojoClass.isEnum())
            {
                newValue = ConvertUtils.convertTo((String) NodeUtils.getNodeValue(node), pojoClass);
            }
            else if (pojoClass.getDeclaredConstructors().length > 0)
            {
                List<Object> arguments = new ArrayList<Object>();
                Constructor<?> declaredConstructor = pojoClass.getDeclaredConstructors()[0];
                Annotation[][] parameterAnnotations = declaredConstructor.getParameterAnnotations();
                for (Annotation[] parameterAnnotation : parameterAnnotations)
                {
                    if (parameterAnnotation.length == 0)
                    {
                        break;
                    }
                    if (parameterAnnotation[0].annotationType().equals(Value.class))
                    {
                        arguments.add(NodeUtils.getNodeValue(node));
                    }
                    else if (parameterAnnotation[0].annotationType().equals(Key.class))
                    {
                        arguments.add(fieldName);
                    }

                }
try {
    newValue = declaredConstructor.newInstance(arguments.toArray(new Object[arguments.size()]));
}catch(IllegalArgumentException e){
    throw new RuntimeException(declaredConstructor+" args: "+arguments+" (size "+arguments.size()+")",e);
}
            }
            else
            {
                newValue = pojoClass.newInstance();
            }
            ReflectionUtils.setProperty(parent, fieldName, newValue);
            processPojoAnnotations(newValue, fieldName, parent);
            return newValue;
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void buildKey(Object parent, ScalarNode node)
    {
        fieldName = node.getValue();
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public Class<?> getPojoClass()
    {
        return pojoClass;
    }

    public String toString()
    {
        return fieldName;
    }
}
