package net.ralama;

import net.md_5.bungee.api.ChatColor;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Attribute;
import org.fusesource.jansi.Ansi.Color;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColoredConsole {
    private static final ColoredConsole instance = new ColoredConsole();
    private static final char ANSI_ESC_CHAR = '\u001b';
    private static final String RGB_STRING = '\u001b' + "[38;2;%d;%d;%dm";
    private static final Pattern RBG_TRANSLATE = Pattern.compile('§' + "x(" + '§' + "[A-F0-9]){6}", 2);
    private final Map<ChatColor, String> replacements = new HashMap<>();
    private final ChatColor[] colors = ChatColor.values();
    public static PrintStream printStream;

    private ColoredConsole() {
        this.replacements.put(ChatColor.BLACK, Ansi.ansi().a(Attribute.RESET).fg(Color.BLACK).boldOff().toString());
        this.replacements.put(ChatColor.DARK_BLUE, Ansi.ansi().a(Attribute.RESET).fg(Color.BLUE).boldOff().toString());
        this.replacements.put(ChatColor.DARK_GREEN, Ansi.ansi().a(Attribute.RESET).fg(Color.GREEN).boldOff().toString());
        this.replacements.put(ChatColor.DARK_AQUA, Ansi.ansi().a(Attribute.RESET).fg(Color.CYAN).boldOff().toString());
        this.replacements.put(ChatColor.DARK_RED, Ansi.ansi().a(Attribute.RESET).fg(Color.RED).boldOff().toString());
        this.replacements.put(ChatColor.DARK_PURPLE, Ansi.ansi().a(Attribute.RESET).fg(Color.MAGENTA).boldOff().toString());
        this.replacements.put(ChatColor.GOLD, Ansi.ansi().a(Attribute.RESET).fg(Color.YELLOW).boldOff().toString());
        this.replacements.put(ChatColor.GRAY, Ansi.ansi().a(Attribute.RESET).fg(Color.WHITE).boldOff().toString());
        this.replacements.put(ChatColor.DARK_GRAY, Ansi.ansi().a(Attribute.RESET).fg(Color.BLACK).bold().toString());
        this.replacements.put(ChatColor.BLUE, Ansi.ansi().a(Attribute.RESET).fg(Color.BLUE).bold().toString());
        this.replacements.put(ChatColor.GREEN, Ansi.ansi().a(Attribute.RESET).fg(Color.GREEN).bold().toString());
        this.replacements.put(ChatColor.AQUA, Ansi.ansi().a(Attribute.RESET).fg(Color.CYAN).bold().toString());
        this.replacements.put(ChatColor.RED, Ansi.ansi().a(Attribute.RESET).fg(Color.RED).bold().toString());
        this.replacements.put(ChatColor.LIGHT_PURPLE, Ansi.ansi().a(Attribute.RESET).fg(Color.MAGENTA).bold().toString());
        this.replacements.put(ChatColor.YELLOW, Ansi.ansi().a(Attribute.RESET).fg(Color.YELLOW).bold().toString());
        this.replacements.put(ChatColor.WHITE, Ansi.ansi().a(Attribute.RESET).fg(Color.WHITE).bold().toString());
        this.replacements.put(ChatColor.MAGIC, Ansi.ansi().a(Attribute.BLINK_SLOW).toString());
        this.replacements.put(ChatColor.BOLD, Ansi.ansi().a(Attribute.UNDERLINE_DOUBLE).toString());
        this.replacements.put(ChatColor.STRIKETHROUGH, Ansi.ansi().a(Attribute.STRIKETHROUGH_ON).toString());
        this.replacements.put(ChatColor.UNDERLINE, Ansi.ansi().a(Attribute.UNDERLINE).toString());
        this.replacements.put(ChatColor.ITALIC, Ansi.ansi().a(Attribute.ITALIC).toString());
        this.replacements.put(ChatColor.RESET, Ansi.ansi().a(Attribute.RESET).toString());
    }

    public static void sendMessage(String message) {
        instance.sendMessage0(message);
    }

    private static String convertRGBColors(String input) {
        Matcher matcher = RBG_TRANSLATE.matcher(input);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String s = matcher.group().replace("§", "").replace('x', '#');
            java.awt.Color color = java.awt.Color.decode(s);
            int red = color.getRed();
            int blue = color.getBlue();
            int green = color.getGreen();
            String replacement = String.format(RGB_STRING, red, green, blue);
            matcher.appendReplacement(buffer, replacement);
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    public void sendMessage0(String message) {
        String result = convertRGBColors(message);
        ChatColor[] var6;
        int var5 = (var6 = this.colors).length;

        for (int var4 = 0; var4 < var5; ++var4) {
            ChatColor color = var6[var4];
            if (this.replacements.containsKey(color)) {
                result = result.replaceAll("(?i)" + color.toString(), this.replacements.get(color));
            } else {
                result = result.replaceAll("(?i)" + color.toString(), "");
            }
        }
        printStream.println(result + Ansi.ansi().reset().toString());
    }
}
