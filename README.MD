# Basic Economy

An extremely basic Vault economy plugin for Spigot, Paper, and Folia servers.

## Requirements

- Java 8
- [Spigot](https://www.spigotmc.org/), [Paper](https://papermc.io/downloads/paper), or [Folia](https://papermc.io/software/folia) 1.8.8-1.19.4
- [BlueSlimeCore](https://hangar.papermc.io/SirBlobman/BlueSlimeCore)
- Vault


## Folia

If your server is running Folia, you need a version of Vault that supports it.
I have created one here, but there may be others.

[Vault-Folia](https://github.com/SirBlobman/Vault-Folia/releases)

## Commands

| Command   | Permission              | Description                                          | Usage                                   |
|-----------|-------------------------|------------------------------------------------------|-----------------------------------------|
| /balance  | economy.command.balance | Check the balance of a player.                       | /balance &lsqb;player&rsqb;             |
| /eco-set  | economy.command.set     | Change the balance of a player to a specific number. | /eco-set &lt;player&gt; &lt;amount&gt;  |
| /eco-give | economy.command.give    | Give some money to a player.                         | /eco-give &lt;player&gt; &lt;amount&gt; |
| /eco-take | economy.command.take    | Remove some money from a player.                     | /eco-take &lt;player&gt; &lt;amount&gt; |
