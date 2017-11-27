/*
Yaaic - Yet Another Android IRC Client

Copyright 2009-2013 Sebastian Kaspari

This file is part of Yaaic.

Yaaic is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Yaaic is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Yaaic.  If not, see <http://www.gnu.org/licenses/>.
 */
package vn.edu.usth.irc.Command;

import android.content.Intent;

import java.util.HashMap;

import vn.edu.usth.irc.Command.handler.JoinHandler;
import vn.edu.usth.irc.Command.handler.NamesHandler;
import vn.edu.usth.irc.Command.handler.NickHandler;
import vn.edu.usth.irc.IRC.IRCService;
import vn.edu.usth.irc.Model.Broadcast;
import vn.edu.usth.irc.Model.Chat;
import vn.edu.usth.irc.Model.Message;
import vn.edu.usth.irc.Model.Server;

/**
 * Parser for commands
 * 
 * @author Sebastian Kaspari <sebastian@yaaic.org>
 */
public class CommandParser
{
    private final HashMap<String, BaseHandler> commands;
    private final HashMap<String, String> aliases;
    private static CommandParser instance;

    /**
     * Create a new CommandParser instance
     */
    private CommandParser()
    {
        commands = new HashMap<String, BaseHandler>();

        // Commands
        commands.put("nick", new NickHandler());
        commands.put("join", new JoinHandler());
//        commands.put("me", new MeHandler());
        commands.put("names", new NamesHandler());
//        commands.put("echo", new EchoHandler());
//        commands.put("topic", new TopicHandler());
//        commands.put("quit", new QuitHandler());
//        commands.put("op", new OpHandler());
//        commands.put("voice", new VoiceHandler());
//        commands.put("deop", new DeopHandler());
//        commands.put("devoice", new DevoiceHandler());
//        commands.put("kick", new KickHandler());
//        commands.put("query", new QueryHandler());
//        commands.put("part", new PartHandler());
//        commands.put("close", new CloseHandler());
//        commands.put("notice", new NoticeHandler());
//        commands.put("mode", new ModeHandler());
//        commands.put("help", new HelpHandler());
//        commands.put("away", new AwayHandler());
//        commands.put("back", new BackHandler());
//        commands.put("whois", new WhoisHandler());
//        commands.put("msg", new MsgHandler());
//        commands.put("quote", new RawHandler());
//        commands.put("amsg", new AMsgHandler());

        aliases = new HashMap<String, String>();

        // Aliases
        aliases.put("j","join");
        aliases.put("q", "query");
        aliases.put("h", "help");
        aliases.put("raw", "quote");
        aliases.put("w", "whois");
    }

    /**
     * Get the global CommandParser instance
     * 
     * @return
     */
    public static synchronized CommandParser getInstance()
    {
        if (instance == null) {
            instance = new CommandParser();
        }

        return instance;
    }

    /**
     * Get the commands HashMap
     * 
     * @return HashMap - command, commandHandler
     */
    public HashMap<String, BaseHandler> getCommands()
    {
        return commands;
    }

    /**
     * Get the command aliases HashMap
     * 
     * @return HashMap - alias, command the alias belogs to
     */
    public HashMap<String, String> getAliases()
    {
        return aliases;
    }

    /**
     * Is the given command a valid client command?
     * 
     * @param command The (client) command to check (/command)
     * @return true if the command can be handled by the client, false otherwise
     */
    public boolean isClientCommand(String command)
    {
        return commands.containsKey(command.toLowerCase()) || aliases.containsKey(command.toLowerCase());
    }

    /**
     * Handle a client command
     * 
     * @param type Type of the command (/type param1 param2 ..)
     * @param params The parameters of the command (0 is the command itself)
     * @param server The current server
     * @param conversation The selected conversation
     * @param service The service handling the connections
     */
    public void handleClientCommand(String type, String[] params, Server server, Chat conversation, IRCService service)
    {
        BaseHandler command = null;

        if (commands.containsKey(type.toLowerCase())) {
            command = commands.get(type.toLowerCase());
        } else if (aliases.containsKey(type.toLowerCase())) {
            String commandInCommands = aliases.get(type.toLowerCase());
            command = commands.get(commandInCommands);
        }
        command.execute(params, server, conversation, service);

        if (conversation != null) {
            Message errorMessage = new Message(type + ": ");
            errorMessage.setColor(Message.COLOR_RED);
            conversation.addMessage(errorMessage);

            // XXX:I18N - How to get a context here? (command_syntax)
            Message usageMessage = new Message("Syntax: " + command.getUsage());
            conversation.addMessage(usageMessage);

            Intent intent = Broadcast.createConversationIntent(
                Broadcast.CONVERSATION_MESSAGE,
                server.getId(),
                conversation.getName()
            );

            service.sendBroadcast(intent);
        }
    }

    /**
     * Handle a server command
     * 
     * @param type Type of the command (/type param1 param2 ..)
     * @param params The parameters of the command (0 is the command itself)
     * @param server The current server
     * @param conversation The selected conversation
     * @param service The service handling the connections
     */
    public void handleServerCommand(String type, String[] params, Server server, Chat conversation, IRCService service)
    {
        if (params.length > 1) {
            service.getConnection(server.getId()).sendRawLineViaQueue(
                type.toUpperCase() + " " + BaseHandler.mergeParams(params)
            );
        } else {
            service.getConnection(server.getId()).sendRawLineViaQueue(type.toUpperCase());
        }
    }

    /**
     * Parse the given line
     * 
     * @param line
     */
    public void parse(String line, Server server, Chat conversation, IRCService service)
    {
        line = line.trim().substring(1); // cut the slash
        String[] params = line.split(" ");
        String type = params[0];

        if (isClientCommand(type)) {
            handleClientCommand(type, params, server, conversation, service);
        } else {
            handleServerCommand(type, params, server, conversation, service);
        }
    }
}
