package com.coinomi.core.coins;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.Networks;

import com.coinomi.core.uri.CoinURIParseException;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Set;

/**
 * @author John L. Jegutanis
 */
public enum CoinID {
    GROESTLCOIN_MAIN(GroestlCoinMain.get())
    ;

    static {
        Set<NetworkParameters> bitcoinjNetworks = Networks.get();
        for (NetworkParameters network : bitcoinjNetworks) {
            Networks.unregister(network);
        }

        for (CoinID id : values()) {
            Networks.register(id.type);
        }
    }

    private final CoinType type;

    private CoinID(final CoinType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type.getId();
    }

    public CoinType getCoinType() {
        return type;
    }

    public static List<CoinType> getSupportedCoins() {
        ImmutableList.Builder<CoinType> builder = ImmutableList.builder();
        for (CoinID id : values()) {
            builder.add(id.type);
        }
        return builder.build();
    }

    public static CoinType typeFromId(String stringId) {
        return fromId(stringId).type;
    }

    public static CoinID fromId(String stringId) {
        for(CoinID id : values()) {
            if (id.type.getId().equalsIgnoreCase(stringId)) return id;
        }
        throw new IllegalArgumentException("Unsupported ID: " + stringId);
    }

    public static CoinID fromUri(String input) {
        for(CoinID id : values()) {
            if (input.startsWith(id.getCoinType().getUriScheme() + "://")) {
                return id;
            } else if (input.startsWith(id.getCoinType().getUriScheme()+":")) {
                return id;
            }
        }
        throw new IllegalArgumentException("Unsupported URI: " + input);
    }

    public static CoinType typeFromAddress(String address) throws AddressFormatException {
        NetworkParameters addressParams = new Address(null, address).getParameters();
        if (addressParams instanceof CoinType) {
            return (CoinType) addressParams;
        } else {
            throw new AddressFormatException("Unsupported address network: " + addressParams.getId());
        }
    }

    public static CoinType typeFromSymbol(String symbol) {
        for(CoinID id : values()) {
            if (id.type.getSymbol().equalsIgnoreCase(symbol)) return id.type;
        }
        throw new IllegalArgumentException("Unsupported coin symbol: " + symbol);
    }
}