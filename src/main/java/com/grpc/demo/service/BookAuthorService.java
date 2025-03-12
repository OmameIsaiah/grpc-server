package com.grpc.demo.service;

import com.grpc.basics.proto.Author;
import com.grpc.basics.proto.BookAuthorServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;
import io.grpc.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@GrpcService
public class BookAuthorService extends BookAuthorServiceGrpc.BookAuthorServiceImplBase {

    static List<Author> getAuthorsFromTempDb() {
        return new ArrayList<>() {
            {
                add(Author.newBuilder().setAuthorId(1).setBookId(1).setFirstName("John").setLastName("Doe").setGender("M").build());
                add(Author.newBuilder().setAuthorId(2).setBookId(2).setFirstName("Faith").setLastName("Michael").setGender("F").build());
                add(Author.newBuilder().setAuthorId(3).setBookId(3).setFirstName("Ruth").setLastName("Peter").setGender("F").build());
                add(Author.newBuilder().setAuthorId(4).setBookId(4).setFirstName("Michael").setLastName("Abraham").setGender("M").build());
            }
        };
    }

    @Override
    public void getAuthor(Author request, StreamObserver<Author> responseObserver) {
        AtomicBoolean valueRetrieved = new AtomicBoolean(true);
        getAuthorsFromTempDb().stream()
                .filter(author -> author.getAuthorId() == request.getAuthorId())
                .findFirst()
                .ifPresentOrElse(
                        responseObserver::onNext,
                        () -> {
                            valueRetrieved.set(false);
                            responseObserver.onError(
                                    Status.NOT_FOUND
                                            .withDescription("Author with ID " + request.getAuthorId() + " not found.")
                                            .asRuntimeException()
                            );
                        }
                );
        if (Objects.equals(Boolean.TRUE, valueRetrieved.get())) {
            responseObserver.onCompleted();
        }
    }
}
