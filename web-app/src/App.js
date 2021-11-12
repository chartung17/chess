import React from 'react';
import Square from './Square';
import './App.css';

export default class App extends React.Component {
  constructor(props) {
    super(props);
    let board = [];
    for (let i = 0; i < 64; i++) {
      board.push(<Square
        number={i}
        char={'0'}
        selected={false}
        onClick={this.selectsquare(i)}
      />)
    }
    this.state = {
      board: board,
      buttons: [],
      message1: '',
      message2: 'Loading...',
      buttonsStr: '',
      boardStr: '',
      movesStr: '',
      sessionID: 'error'
    }
    this.selectsquare = this.selectsquare.bind(this);
  }

  componentDidMount() {
    fetch("https://chartung17-chess.herokuapp.com/", {
      method: 'GET'
    })
    .then(res => {
      return res.json();
    }, err => {
      // Print the error if there is one.
      console.log(err);
    }).then(result => {
      if (result === undefined) {
        console.log('Unknown error occured');
      } else if (result['status'] === 200) {
        var boardStr = result['board'];
        var movesStr = result['moves'];
        var message1 = result['message1'];
        var message2 = result['message2'];
        var buttonsStr = result['buttons'];
        var session = result['session'];
      }
      let board = [];
      for (let i = 0; i < 64; i++) {
        board.push(<Square
          number={i}
          key={i}
          char={boardStr.charAt(i)}
          selected={movesStr.charAt(i) === 'X'}
          onClick={this.selectsquare(i)}
        />)
      }
      let buttons = buttonsStr.map(text => <button key={text} onClick={this.handleButton(text)}>{text}</button>);
      this.setState({
        board: board,
        buttons: buttons,
        message1: message1,
        message2: message2,
        buttonsStr: buttonsStr,
        boardStr: boardStr,
        movesStr: movesStr,
        sessionID: session
      });
    });
  }

  selectsquare(n) {
    return () => {
      fetch("https://chartung17-chess.herokuapp.com/square/" + this.state.sessionID + "/" + Math.floor(n / 8) + "/" + (n % 8), {
        method: 'GET'
      })
      .then(res => {
        return res.json();
      }, err => {
        // Print the error if there is one.
        console.log(err);
      }).then(result => {
        if (result === undefined) {
          console.log('Unknown error occured');
        } else if (result['status'] === 200) {
          var boardStr = result['board'];
          var movesStr = result['moves'];
          var message1 = result['message1'];
          var message2 = result['message2'];
          var buttonsStr = result['buttons'];
        }
        let board = [];
        for (let i = 0; i < 64; i++) {
          board.push(<Square
            number={i}
            char={boardStr.charAt(i)}
            selected={movesStr.charAt(i) === 'X'}
            onClick={this.selectsquare(i)}
          />)
        }
        let buttons = buttonsStr.map(text => <button key={text} onClick={this.handleButton(text)}>{text}</button>);
        this.setState({
          board: board,
          buttons: buttons,
          message1: message1,
          message2: message2,
          buttonsStr: buttonsStr,
          boardStr: boardStr,
          movesStr: movesStr
        });
      });
    };
  }

  handleButton(text) {
    return () => {
      console.log("https://chartung17-chess.herokuapp.com/button/" + this.state.sessionID + "/" + text.toLowerCase().replace(/ /g, '_'));
      fetch("https://chartung17-chess.herokuapp.com/button/" + this.state.sessionID + "/" + text.toLowerCase().replace(/ /g, '_'), {
        method: 'GET'
      })
      .then(res => {
        return res.json();
      }, err => {
        // Print the error if there is one.
        console.log(err);
      }).then(result => {
        if (result === undefined) {
          console.log('Unknown error occured');
        } else if (result['status'] === 200) {
          var boardStr = result['board'];
          var movesStr = result['moves'];
          var message1 = result['message1'];
          var message2 = result['message2'];
          var buttonsStr = result['buttons'];
        }
        let board = [];
        for (let i = 0; i < 64; i++) {
          board.push(<Square
            number={i}
            char={boardStr.charAt(i)}
            selected={movesStr.charAt(i) === 'X'}
            onClick={this.selectsquare(i)}
          />)
        }
        let buttons = buttonsStr.map(text => <button key={text} onClick={this.handleButton(text)}>{text}</button>);
        this.setState({
          board: board,
          buttons: buttons,
          message1: message1,
          message2: message2,
          buttonsStr: buttonsStr,
          boardStr: boardStr,
          movesStr: movesStr
        });
      });
    };
  }

  render() {
    return (
      <div className="App">
      <div className="Message">
      <p>{this.state.message1}</p>
      <p>{this.state.message2}</p>
      </div>
      <div className="Board">
      {this.state.board}
      </div>
      <div className="Buttons">
      {this.state.buttons}
      </div>
      </div>
    );
  }
}
