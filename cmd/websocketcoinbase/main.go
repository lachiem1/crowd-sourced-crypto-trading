package main

import (
	"encoding/json"
	"fmt"
	"log"
	"os"
	"os/signal"
	"time"

	gorillaWebSocket "github.com/gorilla/websocket"
	"github.com/lachiem1/crowd-sourced-crypto-trading/internal/config"
	"github.com/lachiem1/crowd-sourced-crypto-trading/internal/websocket"
)

// defining individual channels to subscribe to
type SubscribeMessage struct {
	Type 	   string   `json:"type"`
	ProductIds []string `json:"product_ids"`
	Channel    string 	`json:"channel"`
	Signature  string   `json:"signature,omitempty"`
	Key 	   string   `json:"key,omitempty"`
	Passphrase string   `json:"passphrase,omitempty"`
	Timestamp  string   `json:"timestamp,omitempty"`
}

func main() {
	env, err := config.LoadEnv()
	if err != nil {
		log.Fatal("could not load environment vars, exiting program.")
	}

	// program shutdown
	interrupt := make(chan os.Signal, 1)
	signal.Notify(interrupt, os.Interrupt)

	// instantiate new object of type Client (my client for websocket connection to coinbase)
	client := websocket.NewClient()

	// establish websocket connection
	uri := env.CoinbaseMarketDataUri
	err = client.Connect(uri)
	if err != nil {
		log.Fatalf("failed to connect: %v", err)
	}
	defer client.Close()

	// goroutine to run processMessagesErrors on separate thread, so I don't block rest of program
	go processMessagesErrors(client, uri)

	// message I send to coinbase via websocket to control which info they send
	// from coinbase docs: need to send subscribe message < 5 secs after connection established
	// https://docs.cdp.coinbase.com/coinbase-app/advanced-trade-apis/websocket/websocket-overview
	subscribeMsg := SubscribeMessage{
		Type: "subscribe",
		ProductIds: []string{"BTC-USD"},
		Channel: "level2",
	}
	log.Println(subscribeMsg)

	subscribeMsgBytes, err := json.Marshal(subscribeMsg)
	if err != nil {
		log.Fatalf("failed to convert subscribe message struct to json.")
	}

	time.Sleep(1 * time.Second)
	if client.IsConnected() {
		err = client.WriteMessage(gorillaWebSocket.TextMessage, subscribeMsgBytes)
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
