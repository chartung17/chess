import React from 'react';
import './App.css';
import blackBishop from './blackBishop.png';
import blackKing from './blackKing.png';
import blackKnight from './blackKnight.png';
import blackPawn from './blackPawn.png';
import blackQueen from './blackQueen.png';
import blackRook from './blackRook.png';
import whiteBishop from './whiteBishop.png';
import whiteKing from './whiteKing.png';
import whiteKnight from './whiteKnight.png';
import whitePawn from './whitePawn.png';
import whiteQueen from './whiteQueen.png';
import whiteRook from './whiteRook.png';

export default class Square extends React.Component {
  constructor(props) {
    super(props);
    let row = Math.floor(this.props.number / 8);
    let col = this.props.number % 8;
    let color = ((row + col) % 2) === 0 ? 'ghostwhite' : '#333333';
    let image = this.getImage();
    this.state = {
      row: row,
      col: col,
      defaultColor: color,
      color: this.props.selected ? 'yellow' : color,
      image: image,
      onClick: this.props.onClick
    }
    this.getStyle = this.getStyle.bind(this);
    this.getImage = this.getImage.bind(this);
  }

  getStyle() {
    return {
      background: this.state.color,
      border: 0,
      box_shadow: 'none'
    }
  }

  getImage() {
    let image = '';
    switch (this.props.char) {
      case 'B':
      image = blackBishop;
      break;
      case 'K':
      image = blackKing;
      break;
      case 'N':
      image = blackKnight;
      break;
      case 'P':
      image = blackPawn;
      break;
      case 'Q':
      image = blackQueen;
      break
      case 'R':
      image = blackRook;
      break;
      case 'b':
      image = whiteBishop;
      break;
      case 'k':
      image = whiteKing;
      break;
      case 'n':
      image = whiteKnight;
      break;
      case 'p':
      image = whitePawn;
      break;
      case 'q':
      image = whiteQueen;
      break
      case 'r':
      image = whiteRook;
      break;
    }
    return image;
  }

  componentDidUpdate(prevProps) {
    if (this.props === prevProps) {
      return;
    }
    this.setState({
      image: this.getImage()
    });
  }

  render() {
    return (
      <button style={this.getStyle()} onClick={this.state.onClick}><img src={this.state.image}/></button>
    );
  }
}
