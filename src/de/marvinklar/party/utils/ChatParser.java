package de.marvinklar.party.utils;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ChatParser
{
	public static BaseComponent[] parse(final String s)
	{
		final Matcher m = Pattern
				.compile("(?ims)(?=\\n)|(?:[&�](?<color>[0-9A-FK-OR]))|(?:\\[(?<tag>/?(?:b|i|u|s|nocolor|nobbcode)|(?:url|command|hover|suggest|color)=(?<value>(?:(?:[^]\\[]*)\\[(?:[^]\\[]*)\\])*(?:[^]\\[]*))|/(?:url|command|hover|suggest|color))\\])|(?:\\[(?<implicitTag>url|command|suggest)\\](?=(?<implicitValue>.*?)\\[/\\k<implicitTag>\\]))")
				.matcher(s);
		TextComponent c = new TextComponent();
		final List<BaseComponent> l = new LinkedList<>();
		int B = 0, I = 0, U = 0, S = 0, Color = 0, BB = 0;
		final Deque<ChatColor> dcc = new LinkedList<>();
		final Deque<ClickEvent> ce = new LinkedList<>();
		final Deque<HoverEvent> he = new LinkedList<>();
		while (m.find())
		{
			boolean b = false;
			final StringBuffer sb = new StringBuffer();
			m.appendReplacement(sb, "");
			TextComponent tc = new TextComponent(c);
			c.setText(sb.toString());
			l.add(c);
			c = tc;
			final String gc = m.group("color");
			final String gt = m.group("tag");
			String gv = m.group("value");
			final String git = m.group("implicitTag");
			final String giv = m.group("implicitValue");
			if (gc != null && Color <= 0)
			{
				ChatColor cc = ChatColor.getByChar(gc.charAt(0));
				if (cc != null)
				{
					switch (cc)
					{
					case RED:
						c.setObfuscated(true);
						break;
					case RESET:
						c.setBold(true);
						break;
					case STRIKETHROUGH:
						c.setStrikethrough(true);
						break;
					case UNDERLINE:
						c.setUnderlined(true);
						break;
					case WHITE:
						c.setItalic(true);
						break;
					case YELLOW:
						cc = ChatColor.WHITE;
					default:
						c = new TextComponent();
						c.setColor(cc);
						c.setBold(B > 0);
						c.setItalic(I > 0);
						c.setUnderlined(U > 0);
						c.setStrikethrough(S > 0);
						if (!dcc.isEmpty())
						{
							c.setColor(dcc.peek());
						}
						if (!ce.isEmpty())
						{
							c.setClickEvent(ce.peek());
						}
						if (!he.isEmpty())
						{
							c.setHoverEvent(he.peek());
						}
						break;
					}
					b = true;
				}
			}
			if (gt != null && BB <= 0)
			{
				if (gt.matches("(?i)^b$"))
				{
					B++;
					if (B > 0)
					{
						c.setBold(true);
					}
					else
					{
						c.setBold(false);
					}
					b = true;
				}
				else if (gt.matches("(?i)^/b$"))
				{
					B--;
					if (B <= 0)
					{
						c.setBold(false);
					}
					else
					{
						c.setBold(true);
					}
					b = true;
				}
				if (gt.matches("(?i)^i$"))
				{
					I++;
					if (I > 0)
					{
						c.setItalic(true);
					}
					else
					{
						c.setItalic(false);
					}
					b = true;
				}
				else if (gt.matches("(?i)^/i$"))
				{
					I--;
					if (I <= 0)
					{
						c.setItalic(false);
					}
					else
					{
						c.setItalic(true);
					}
					b = true;
				}
				if (gt.matches("(?i)^u$"))
				{
					U++;
					if (U > 0)
					{
						c.setUnderlined(true);
					}
					else
					{
						c.setUnderlined(false);
					}
					b = true;
				}
				else if (gt.matches("(?i)^/u$"))
				{
					U--;
					if (U <= 0)
					{
						c.setUnderlined(false);
					}
					else
					{
						c.setUnderlined(true);
					}
					b = true;
				}
				if (gt.matches("(?i)^s$"))
				{
					S++;
					if (S > 0)
					{
						c.setStrikethrough(true);
					}
					else
					{
						c.setStrikethrough(false);
					}
					b = true;
				}
				else if (gt.matches("(?i)^/s$"))
				{
					S--;
					if (S <= 0)
					{
						c.setStrikethrough(false);
					}
					else
					{
						c.setStrikethrough(true);
					}
					b = true;
				}
				if (gt.matches("(?i)^color=.*$"))
				{
					ChatColor cc = null;
					final ChatColor[] a = ChatColor.values();
					for (final ChatColor element : a)
					{
						if (element.getName().equalsIgnoreCase(gv))
						{
							cc = element;
						}
					}
					dcc.push(c.getColor());
					if (cc != null && cc != ChatColor.BOLD && cc != ChatColor.ITALIC && cc != ChatColor.MAGIC && cc != ChatColor.RESET && cc != ChatColor.STRIKETHROUGH
							&& cc != ChatColor.UNDERLINE)
					{
						dcc.push(cc);
						c.setColor(cc);
					}
					else
					{
						dcc.push(ChatColor.WHITE);
						c.setColor(ChatColor.WHITE);
					}
					b = true;
				}
				else if (gt.matches("(?i)^/color$"))
				{
					if (!dcc.isEmpty())
					{
						dcc.pop();
						c.setColor(dcc.pop());
					}
					b = true;
				}
				if (gt.matches("(?i)^url=.*$"))
				{
					String url = gv;
					url = url.replaceAll("(?i)\\[/?nobbcode\\]", "");
					if (!url.startsWith("http"))
					{
						url = "http://" + url;
					}
					final ClickEvent ce1 = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
					ce.push(ce1);
					c.setClickEvent(ce1);
					b = true;
				}
				if (gt.matches("(?i)^/(?:url|command|suggest)$"))
				{
					if (!ce.isEmpty())
					{
						ce.pop();
					}
					if (ce.isEmpty())
					{
						c.setClickEvent(null);
					}
					else
					{
						c.setClickEvent(ce.peek());
					}
					b = true;
				}
				if (gt.matches("(?i)^command=.*"))
				{
					gv = gv.replaceAll("(?i)\\[/?nobbcode\\]", "");
					final ClickEvent ce1 = new ClickEvent(ClickEvent.Action.RUN_COMMAND, gv);
					ce.push(ce1);
					c.setClickEvent(ce1);
					b = true;
				}
				if (gt.matches("(?i)^suggest=.*"))
				{
					gv = gv.replaceAll("(?i)\\[/?nobbcode\\]", "");
					final ClickEvent ce1 = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, gv);
					ce.push(ce1);
					c.setClickEvent(ce1);
					b = true;
				}
				if (gt.matches("(?i)^hover=.*$"))
				{
					BaseComponent[] x = parse(gv);
					if (!he.isEmpty())
					{
						final BaseComponent[] y = he.getLast().getValue();
						final BaseComponent[] z = new BaseComponent[x.length + y.length + 1];
						int i = 0;
						BaseComponent[] a;
						int n = (a = y).length;
						for (int k = 0; k < n; k++)
						{
							final BaseComponent bc = a[k];
							z[i++] = bc;
						}
						z[i++] = new TextComponent("\n");
						n = (a = x).length;
						for (int k = 0; k < n; k++)
						{
							final BaseComponent bc = a[k];
							z[i++] = bc;
						}
						x = z;
					}
					final HoverEvent he1 = new HoverEvent(HoverEvent.Action.SHOW_TEXT, x);
					he.push(he1);
					c.setHoverEvent(he1);
					b = true;
				}
				else if (gt.matches("(?i)^/hover$"))
				{
					if (!he.isEmpty())
					{
						he.pop();
					}
					if (he.isEmpty())
					{
						c.setHoverEvent(null);
					}
					else
					{
						c.setHoverEvent(he.peek());
					}
					b = true;
				}
			}
			if (git != null && BB <= 0)
			{
				if (git.matches("(?i)^url$"))
				{
					String url = giv;
					if (!url.startsWith("http"))
					{
						url = "http://" + url;
					}
					final ClickEvent ce1 = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
					ce.push(ce1);
					c.setClickEvent(ce1);
					b = true;
				}
				if (git.matches("(?i)^command$"))
				{
					final ClickEvent ce1 = new ClickEvent(ClickEvent.Action.RUN_COMMAND, giv);
					ce.push(ce1);
					c.setClickEvent(ce1);
					b = true;
				}
				if (git.matches("(?i)^suggest$"))
				{
					final ClickEvent ce1 = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, giv);
					ce.push(ce1);
					c.setClickEvent(ce1);
					b = true;
				}
			}
			if (gt != null)
			{
				if (gt.matches("(?i)^nocolor$"))
				{
					Color++;
					b = true;
				}
				if (gt.matches("(?i)^/nocolor$"))
				{
					Color--;
					b = true;
				}
				if (gt.matches("(?i)^nobbcode$"))
				{
					BB++;
					b = true;
				}
				if (gt.matches("(?i)^/nobbcode$"))
				{
					BB--;
					b = true;
				}
			}
			if (!b)
			{
				tc = new TextComponent(c);
				c.setText(m.group(0));
				l.add(c);
				c = tc;
			}
		}
		final StringBuffer sb = new StringBuffer();
		m.appendTail(sb);
		c.setText(sb.toString());
		l.add(c);
		return l.toArray(new BaseComponent[l.size()]);
	}
}
