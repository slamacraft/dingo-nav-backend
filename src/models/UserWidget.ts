import {Document, model, Schema} from "mongoose";

export type TUserWidget = {
    userId: string
    fixed: boolean
    left: number,
    top: number,
    title: string,
    desc: string,
    html: string,
    createTime: Date
    updateTime: Date
};

/**
 * Mongoose Document based on TUser for TypeScript.
 * https://mongoosejs.com/docs/documents.html
 *
 * TUser
 * @param email:string
 * @param password:string
 * @param avatar:string
 */

export interface IUserWidget extends TUserWidget, Document {
}

const userWidgetSchema: Schema = new Schema({
    userId: {
        type: String,
        required: true,
    },
    fixed: {
        type: Boolean,
        default: false,
        required: true
    },
    left: {
        type: Number,
        default: 64
    },
    top: {
        type: Number,
        default: 64
    },
    title: {
        type: String,
        required: true,
    },
    desc: {
        type: String,
        require: true,
    },
    html: {
        type: String,
    },
    createTime: {
        type: Date,
        default: Date.now,
    },
    updateTime: {
        type: Date,
        default: Date.now,
    },
});

/**
 * Mongoose Model based on TUser for TypeScript.
 * https://mongoosejs.com/docs/models.html
 *
 * TUser
 * @param email:string
 * @param password:string
 * @param avatar:string
 */

const UserWidget = model<IUserWidget>("UserWidget", userWidgetSchema);

export default UserWidget;
