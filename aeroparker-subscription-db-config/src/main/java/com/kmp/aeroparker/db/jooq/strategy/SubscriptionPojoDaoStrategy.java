package com.kmp.aeroparker.db.jooq.strategy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.meta.Definition;

import com.kmp.aeroparker.db.jooq.interfaces.JooqPojoInterface;
import com.kmp.aeroparker.db.jooq.interfaces.JooqRecordInterface;

public class SubscriptionPojoDaoStrategy extends DefaultGeneratorStrategy
{
	/**
	 * Override this method to define the interfaces to be implemented by those
	 * artifacts that allow for custom interface implementation
	 */
	@Override
	public List<String> getJavaClassImplements(final Definition definition, final Mode mode)
	{
		List<String> interfaces = new ArrayList<String>();
		interfaces.add(Serializable.class.getName());
		interfaces.add(Cloneable.class.getName());
		if (mode.equals(Mode.POJO))
		{
			interfaces.add(JooqPojoInterface.class.getName());
		}
		if (mode.equals(Mode.RECORD))
		{
			interfaces.add(JooqRecordInterface.class.getName());
		}
		return interfaces;
	}

	@Override
	public String getJavaClassName(final Definition definition, final Mode mode)
	{
		String className = super.getJavaClassName(definition, mode);
		if (definition.getOutputName()
				.startsWith("ab_"))
		{
			className = className.replaceFirst("Ab", "");
		}

		if (definition.getOutputName()
				.startsWith("crm_"))
		{
			className = className.replaceFirst("Crm", "");
		}
		return className;
	}

	@Override
	public String getJavaMemberName(final Definition definition, final Mode mode)
	{
		String result = getCamelCaseMemberName(definition);
		return result.substring(0, 1)
				.toLowerCase() + result.substring(1);
	}

	@Override
	public String getJavaGetterName(final Definition definition, final Mode mode)
	{
		return "get" + getCamelCaseMemberName(definition);
	}

	@Override
	public String getJavaSetterName(final Definition definition, final Mode mode)
	{
		return "set" + getCamelCaseMemberName(definition);
	}

	private String getCamelCaseMemberName(final Definition definition)
	{
		StringBuilder result = new StringBuilder();

		// [#2515] - Keep trailing underscores
		for (String word : definition.getInputName()
				.split("_", -1))
		{
			// Uppercase first letter of a word
			if (word.length() > 0)
			{
				// [#82] - If a word starts with a digit, prevail the
				// underscore to prevent naming clashes
				if (Character.isDigit(word.charAt(0)))
				{
					result.append("_");
				}

				result.append(word.substring(0, 1)
						.toUpperCase());
				result.append(word.substring(1));
			}

			// If no letter exists, prevail the underscore (e.g. leading
			// underscores)
			else
			{
				result.append("_");
			}
		}

		return result.toString();
	}
}