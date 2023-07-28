import {Document, model, Schema} from "mongoose";
import {IBase, LogicDelete, schemaConfig} from "src/models/base";

export type TUser = {
    email: string;
    password: string;
    name: string;
    avatar: string; // 头像
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

export interface IUser extends TUser, Document {
}

const userSchema: Schema = new Schema(Object.assign({
    email: {
        type: String,
        required: true,
        unique: true,
    },
    password: {
        type: String,
        required: true,
    },
    name: {
        type: String,
        require: true,
    },
    avatar: {
        type: String,
    },
}, IBase), schemaConfig);

/**
 * Mongoose Model based on TUser for TypeScript.
 * https://mongoosejs.com/docs/models.html
 *
 * TUser
 * @param email:string
 * @param password:string
 * @param avatar:string
 */

const User = model<IUser, LogicDelete<IUser, any>>("User", userSchema);

export default User;
