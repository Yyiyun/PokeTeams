package io.github.tsecho.poketeams.commands.admin;

import io.github.tsecho.poketeams.apis.PokeTeamsAPI;
import io.github.tsecho.poketeams.enums.messages.ErrorMessages;
import io.github.tsecho.poketeams.enums.messages.SuccessMessages;
import io.github.tsecho.poketeams.enums.Ranks;
import io.github.tsecho.poketeams.language.Texts;
import io.github.tsecho.poketeams.utilities.ErrorCheck;
import io.github.tsecho.poketeams.utilities.Permissions;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

public class Promote implements CommandExecutor {

    private User player;
    private PokeTeamsAPI role;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        player = args.<User>getOne(Text.of("player")).get();
        role = new PokeTeamsAPI(player.getName(), false);

        if(!role.inTeam())
            return ErrorCheck.test(src, ErrorMessages.NOT_IN_TEAM);
        if(role.getPlace() == 3)
            return ErrorCheck.test(src, ErrorMessages.CANT_PROMOTE);

        role.addTeamMember(player.getName(), Ranks.getEnum(role.getPlace() + 1).getName());
        player.getPlayer().ifPresent(p -> p.sendMessage(SuccessMessages.PROMOTED.getText(p)));
        src.sendMessage(Texts.of(SuccessMessages.PROMOTE_SEND.getString(), player.getName()));

        return CommandResult.success();
    }

    public static CommandSpec build() {
        return CommandSpec.builder()
                .permission(Permissions.ADMIN_PROMOTE)
                .arguments(GenericArguments.onlyOne(GenericArguments.user(Text.of("player"))))
                .executor(new Promote())
                .build();
    }
}