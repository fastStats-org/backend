package org.faststats.controller;

import org.faststats.FastStats;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

@NullMarked
public class DatabaseController {
    private final Connection connection;

    public DatabaseController(FastStats fastStats) throws SQLException {
        var file = new File(fastStats.dataFolder(), "database.db");
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + file);
    }


    private <T> T executeQuery(String query, ThrowingFunction<ResultSet, T> mapper, Object... parameters) throws SQLException {
        try (var preparedStatement = connection.prepareStatement(query)) {
            for (var i = 0; i < parameters.length; i++)
                preparedStatement.setObject(i + 1, parameters[i]);
            try (var resultSet = preparedStatement.executeQuery()) {
                return ThrowingFunction.unchecked(mapper).apply(resultSet);
            }
        }
    }

    private int executeUpdate(String query, Object... parameters) throws SQLException {
        try (var preparedStatement = connection.prepareStatement(query)) {
            for (var i = 0; i < parameters.length; i++)
                preparedStatement.setObject(i + 1, parameters[i]);
            return preparedStatement.executeUpdate();
        }
    }

    private interface ThrowingFunction<T, R> {
        R apply(T t) throws SQLException;

        static <T, R> ThrowingFunction<T, R> unchecked(ThrowingFunction<T, R> f) {
            return f;
        }
    }
}
