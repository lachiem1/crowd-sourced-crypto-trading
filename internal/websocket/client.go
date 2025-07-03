package websocket

import (
	"fmt"
	"log"
	"net/url"
	"sync"
	"time"

	"github.com/gorilla/websocket"
)

// websocket client connection to coinbase, holds connection and channels for managing messages
type Client struct {
	conn *websocket.Conn
	mu   sync.Mutex
	// buffered channels to prevent blocking if receiver isn't ready
	// sends messages and errors out of the client struct
	Messages chan []byte
	Errors   chan error

	// channels to stop the client
	// done signals reader goroutine is done, closeCh signals to close connection
	done    chan struct{}
	closeCh chan struct{}
}

// method to return new Client instance
func NewClient() *Client {
	return &Client{
		Messages: make(chan []byte, 100),
		Errors:   make(chan error, 10),
		done:     make(chan struct{}),
		closeCh:  make(chan struct{}),
	}
}

func (c *Client) Connect(wsURL string) error {
	url, err := url.Parse(wsURL)
	if err != nil {
		return fmt.Errorf("invalid websocket URL: %w", err)
	}
	// Dial function returns: (*connection, *response, error) 
	conn, _, err := websocket.DefaultDialer.Dial(url.String(), nil)
	if err != nil {
		return fmt.Errorf("error connecting to websocket: %w", err)
	}
	c.conn = conn

	// start this goroutine immediately once connection is success
	go c.readMessages()

	return nil
}

func (c *Client) readMessages() {
	// run this anon function to close connection even if readMessages goroutine/thread exits with error
	defer func() {
		// close message and error channels when reader stops
		close(c.Messages)
		close(c.Errors)
		close(c.done)
		if c.conn != nil {
			c.conn.Close()
		}
	}()

	// go's version of the infinite while loop :-)
	for {
		select {
		// close signal received
		case <-c.closeCh:
			log.Println("closing websocket connection from reader")
			return
		// non-blocking default case, i.e. allow select to continue
		default:
		}

		_, msg, err := c.conn.ReadMessage()
		if err != nil {
			// check if normal close error
			if websocket.IsCloseError(err, websocket.CloseNormalClosure, websocket.CloseGoingAway, websocket.CloseNoStatusReceived) {
				return
			}
			// send abnormal errors to error chan
			c.Errors <- fmt.Errorf("error reading message: %w", err)
			return
		}
		// send received msg to Messages chan
		c.Messages <- msg
	}
}

// used to write control messages to websocket, like which crypto I want market data for, closing ws connection, etc
func (c *Client) WriteMessage(messageType int, data []byte) error {
	// lock before writing
	c.mu.Lock() 
	// release lock after writing
	defer c.mu.Unlock()
	return c.conn.WriteMessage(messageType, data)
}

func (c *Client) Close() {
	// send signal to reader goroutine to stop
	close(c.closeCh)

	// send close message to coinbase server to gracefully shutdown websocket connection
	err := c.WriteMessage(websocket.CloseMessage, websocket.FormatCloseMessage(websocket.CloseNormalClosure, ""))
	if err != nil {
		log.Println("error connecting to websocket:", err)
	}

	// either the client closes sucessfully or force a time out
	select {
	case <-c.done:
		log.Println("websocket client closed connection successfully")
	case <-time.After(10 * time.Second):
		log.Println("timed out waiting for websocket client to close connection")
	}
}

// public fn to check if websocket connection is active
func (c *Client) IsConnected() bool {
	return c.conn != nil
}