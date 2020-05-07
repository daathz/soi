'use strict';

import * as express from 'express';
import {Movie, MovieEntity} from '../schemas/movie';
import {IMovie, IMovieId, IMovieIdList, IMovieList, IMyMovie} from "../interfaces/movies";

const router = express.Router();

function generate_id(movieList: Array<MovieEntity>) {
    let id: number = 1;
    if (movieList.length) {
        movieList.forEach(movie => {
            if (id <= movie.id) {
                id = movie.id + 1;
            }
        })
    }
    return id;
}

router.get('/find', (req, res) => {
    // @ts-ignore
    let year: number = parseInt(req.query.year);
    if (req.query.orderby === 'Title' || req.query.orderby === 'Director')
        Movie.find({year: year}, 'id', {sort: req.query.orderby.toLowerCase()}, (err, docs) => {
            if (err) {
                res.status(500).json({info: 'Error executing query.', error: err});
            }
            if (docs) {
                let movieIdList: IMovieIdList = {
                    id: docs.map(movie => movie.id)
                }
                res.json(movieIdList);
            }
        })
})

router.get('/', (req, res) => {
    Movie.find({}, (err, docs) => {
        if (err) {
            res.status(500).json({info: 'Error executing query.', error: err});
        }
        if (docs) {
            let movieList: IMovieList = {
                movie: docs.map(doc => {
                    let item: IMovie = {
                        title: doc.title,
                        year: doc.year,
                        director: doc.director,
                        actor: doc.actor
                    };
                    return item;
                })
            };
            res.json(movieList);
        }
    })
})

router.get('/:id', (req, res) => {
    Movie.findOne({id: req.params.id}, (err, doc) => {
        if (err) {
            res.status(500).json({info: 'Error executing query.', error: err});
        }
        if (doc) {
            let movie: IMovie = {
                title: doc.title,
                year: doc.year,
                director: doc.director,
                actor: doc.actor
            };
            res.json(movie);
        } else {
            res.status(404).end();
        }
    })
})

router.post('/', (req, res) => {
    Movie.find({}, (err, movies) => {
        let movie: IMyMovie = {
            title: req.body.title,
            year: req.body.year,
            director: req.body.director,
            actor: req.body.actor,
            id: generate_id(movies)
        };
        Movie.insertMany([movie], (err, docs) => {
            if (err) {
                res.status(500).json({info: 'Error executing query.', error: err});
            }
            if (docs) {
                let movieId: IMovieId = {
                    id: movie.id
                };
                res.json(movieId);
            }
        })
    })
})

router.put('/:id', (req, res) => {
    let actors: string[] = [];
    if (req.body.actor) {
        actors = req.body.actor;
    }
    let movie: IMyMovie = {
        id: parseInt(req.params.id),
        title: req.body.title,
        year: req.body.year,
        director: req.body.director,
        actor: actors
    }
    Movie.findOneAndUpdate({id: req.params.id}, movie, {upsert: true, new: true, runValidators: true}, (err, doc) => {
        if (err) {
            res.status(500).json({info: 'Error executing query.', error: err});
        }
        if (doc) {
            res.status(200).end();
        }
    })
})

router.delete('/:id', (req, res) => {
    Movie.remove({id: req.params.id}, (err) => {
        if (err) {
            res.status(500).json({info: 'Error executing query.', error: err});
        }
        res.status(200).end();
    })
})

export default router;