package main

import (
	"fmt"
	"log"
	"os"
	"os/signal"
	"time"

	"github.com/lachiem1/crowd-sourced-crypto-trading/internal/websocket"
	gorillaWebSocket "github.com/gorilla/websocket"
)

func main() {
	// program shutdown
	interrupt := make(chan os.Signal, 1)
	signal.Notify(interrupt, os.Interrupt)

	// instantiate new object of type Client (my client for websocket connection to coinbase)
	client := websocket.NewClient()

	// establish websocket connection
	uri := "wss://ws-feed.exchange.coinbase.com"
	err := client.Connect(uri)
	if err != nil {
		log.Fatalf("failed to connect: %v", err)
	}
	defer client.Close()

	// goroutine to run processMessagesErrors on separate thread, so I don't block rest of program
	go processMessagesErrors(client, uri)

	// message I send to coinbase via websocket to control which info they send
	// from coinbase docs: need to send subscribe message < 5 secs after connection established
	// https://docs.cdp.coinbase.com/exchange/websocket-feed/authentication
	subscribeMsg := []byte(`{
			"type": "subscribe",
			"product_ids": ["ETH-USD", "BTC-USD"],
			"channels": ["full"],
			"signature": TODO - sig here,
			"key": TODO - key env variable here,
			"passphrase": TODO - passphrase env var here,
			"timestamp": TODO
	}`)
	time.Sleep(1 * time.Second)
	if client.IsConnected() {
		err = client.WriteMessage(gorillaWebSocket.TextMessage, subscribeMsg)
		if err != nil {
			log.Printf("error sending subscribe message: %v", err)
		}
	} else {
		log.Println("cannot send subscribe message: websocket not connected")
	}


	<-interrupt
	log.Println("interrupt signal received, gracefully shutting down")
}

// process received messages and errors from websocket connection
func processMessagesErrors(client *websocket.Client, uri string) {
	for {
			select {
			case msg := <- client.Messages:
				fmt.Printf("msg received from coinbase websocket: %s\n", msg)
				// parse JSON to Kafka here - TODO
			case err := <- client.Errors:
				fmt.Printf("error received from coinbase websocket: %s\n", err)

				if !client.IsConnected() {
					log.Println("websocket connection lost")
					// crude singular retry - TODO: retries with exponential backoff
					time.Sleep(5 * time.Second)
					err := client.Connect(uri)
					// if reconnection attempt fails, log it
					if err != nil {
						log.Printf("reconnection attempt failed: %v", err)
					}
				}
			}
		
		}
}