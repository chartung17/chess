import React from 'react';
import Square from './Square';
import './App.css';

export default class App extends React.Component {
  constructor(props) {
    super(props);
    let board = [];
    for (let i = 0; i < 64; i++) {
      board.push(<Square number={i} char={'0'} selected={false} onClick={this.selectsquare(i)}/>)
    }
    this.state = {
      board: board,
      message1: '',
      message2: '',
      buttonsStr: ''
    }
    this.selectsquare = this.selectsquare.bind(this);
  }

  selectsquare(n) {
    return () => {
      console.log("https://chartung17-chess.herokuapp.com/square/" + Math.floor(n / 8) + "/" + (n % 8));
      fetch("https://chartung17-chess.herokuapp.com/square/" + Math.floor(n / 8) + "/" + (n % 8), {
        method: 'GET'
      })
      .then(res => {
        return res.json();
      }, err => {
        // Print the error if there is one.
        console.log(err);
      }).then(result => {
        console.log(result);
        let text = '';
        let success = false;
        if (result === undefined) {
          console.log('Unknown error occured');
        } else if (result['status'] === 200) {
          var boardStr = result['board'];
          var movesStr = result['moves'];
          var message1 = result['message1'];
          var message2 = result['message2'];
          var buttonsStr = result['buttons'];
        }
        for (let i = 0; i < 64; i++) {
          this.state.board[i] = <Square number={i} char={boardStr.charAt(i)} selected={movesStr.charAt(i) === 'X'} onClick={this.selectsquare(i)}/>;
        }
        this.setState({
          message1: message1,
          message2: message2,
          buttonsStr: buttonsStr
        });
      });
    };
  }

  render() {
    return (
      <div className="Board">
      {this.state.board}
      </div>
    );
  }
}
